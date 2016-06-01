package org.lijingpeng.algo.cpm_predict.mapred;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.MapperBase;
import org.lijingpeng.algo.cpm_predict.utils.Constants;

import java.io.IOException;

public class CpmForecastMapper extends MapperBase {
    private Record mapOutputKey;
    private Record mapOutputValue;

    @Override
    public void setup(TaskContext context) throws IOException {
        mapOutputKey   = context.createMapOutputKeyRecord();
        mapOutputValue = context.createMapOutputValueRecord();
    }

    @Override
    public void map(long key, Record record, TaskContext context) 
        throws IOException {

        Long member_id   = record.getBigint(Constants.TBL_CPM_FIELD_MEMBER_ID);
        Long type        = record.getBigint(Constants.TBL_CPM_FIELD_TYPE);
        String sign      = record.getString(Constants.TBL_CPM_FIELD_SIGN).trim();
        Long quantile    = record.getBigint(Constants.TBL_CPM_FIELD_QUANTILE);
        Double cpm       = record.getDouble(Constants.TBL_CPM_FIELD_CPM);
        String dt        = record.getString(Constants.TBL_CPM_FIELD_DT).trim();
        String adzone_id = 
            record.getString(Constants.TBL_CPM_FIELD_ADZONEID).trim();

        mapOutputKey.setBigint(Constants.TBL_CPM_FIELD_MEMBER_ID, member_id);
        mapOutputKey.setBigint(Constants.TBL_CPM_FIELD_TYPE, type);
        mapOutputKey.setString(Constants.TBL_CPM_FIELD_SIGN, sign);
        mapOutputKey.setString(Constants.TBL_CPM_FIELD_ADZONEID, adzone_id);
        mapOutputKey.setBigint(Constants.TBL_CPM_FIELD_QUANTILE, quantile);

        mapOutputValue.setString(Constants.TBL_CPM_FIELD_DT, dt);
        mapOutputValue.setDouble(Constants.TBL_CPM_FIELD_CPM, cpm);

        context.write(mapOutputKey, mapOutputValue);
    }

    @Override
    public void cleanup(TaskContext context) throws IOException {
        super.cleanup(context);
    }
}
