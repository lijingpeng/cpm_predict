package org.lijingpeng.algo.cpm_predict.mapred;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.ReducerBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lijingpeng.algo.cpm_predict.models.DoubleExpSmoothing;
import org.lijingpeng.algo.cpm_predict.models.DoubleExpSmoothingSolver;
import org.lijingpeng.algo.cpm_predict.utils.Constants;

import java.io.IOException;
import java.util.*;

public class CpmForecastReducer extends ReducerBase
{
    private static final Log log =
        LogFactory.getLog(CpmForecastReducer.class);

    private Record result;

    private List<Record> iterator2List(TaskContext context, 
            Iterator<Record> iter) throws IOException 
    {

        List<Record> list = new ArrayList<Record>();

        Record tmp, listRecord;
        while (iter.hasNext()) {
            tmp = iter.next();

            listRecord = context.createMapOutputValueRecord();
            listRecord.setString(Constants.TBL_CPM_FIELD_DT,
                    tmp.getString(Constants.TBL_CPM_FIELD_DT));
            listRecord.setDouble(Constants.TBL_OUT_FIELD_CPM, 
                    tmp.getDouble(Constants.TBL_CPM_FIELD_CPM));

            list.add(listRecord);
        }

        return list;
    }

    @Override
    public void setup(TaskContext context) throws IOException
    {
        result = context.createOutputRecord();
    }

    @Override
    public void reduce(Record key, Iterator<Record> values, 
            TaskContext context) throws IOException, IllegalArgumentException
    {

        Long member_id   = key.getBigint(Constants.TBL_CPM_FIELD_MEMBER_ID);
        Long type        = key.getBigint(Constants.TBL_CPM_FIELD_TYPE);
        String sign      = key.getString(Constants.TBL_CPM_FIELD_SIGN);
        Long quantile    = key.getBigint(Constants.TBL_CPM_FIELD_QUANTILE);
        String adzone_id = key.getString(Constants.TBL_CPM_FIELD_ADZONEID);

        List<Record> valueList = iterator2List(context, values);

        Collections.sort(valueList, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                String dt1 = r1.getString(Constants.TBL_CPM_FIELD_DT);
                String dt2 = r2.getString(Constants.TBL_CPM_FIELD_DT);

                return dt1.compareTo(dt2);
            }
        });

        double[] cpmArray = new double[valueList.size()];
        double maxCpm = -1.0;
        int i = 0;
        for (Record r : valueList) {
            Double tmp = r.getDouble(Constants.TBL_CPM_FIELD_CPM);

            cpmArray[i] = tmp.doubleValue();

            if (cpmArray[i] > maxCpm) {
                maxCpm = cpmArray[i];
            }

            i++;
        }

        double[] forecasted = new double[2];
        forecasted[0] = maxCpm;
        forecasted[1] = 5.0;
        
        try
        {
            double[] params = DoubleExpSmoothingSolver.solve(cpmArray);
            double alpha = params[0];
            double beta  = params[1];
            forecasted = DoubleExpSmoothing.forecast(cpmArray, alpha, beta); 

            log.info(String.format("alpha=%s, beta=%s", alpha, beta));
        }
        catch (IllegalArgumentException e)
        {
            log.error(e.getMessage());
        }

        log.info("Forecast result: " + Arrays.toString(forecasted));

        result.setBigint(Constants.TBL_OUT_FIELD_MEMBER_ID, member_id);
        result.setBigint(Constants.TBL_OUT_FIELD_TYPE, type);
        result.setString(Constants.TBL_OUT_FIELD_SIGN, sign);
        result.setString(Constants.TBL_OUT_FIELD_ADZONEID, adzone_id);
        result.setBigint(Constants.TBL_OUT_FIELD_QUANTILE, quantile);
        result.setDouble(Constants.TBL_OUT_FIELD_CPM, forecasted[0]);
        result.setDouble(Constants.TBL_OUT_FIELD_CPM_INTVL, forecasted[1]);

        context.write(result);
    }
}
