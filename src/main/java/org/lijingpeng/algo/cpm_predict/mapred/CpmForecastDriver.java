package org.lijingpeng.algo.cpm_predict.mapred;

import com.aliyun.odps.OdpsException;
import com.aliyun.odps.data.TableInfo;
import com.aliyun.odps.mapred.JobClient;
import com.aliyun.odps.mapred.conf.JobConf;
import com.aliyun.odps.mapred.utils.InputUtils;
import com.aliyun.odps.mapred.utils.OutputUtils;
import com.aliyun.odps.mapred.utils.SchemaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lijingpeng.algo.cpm_predict.utils.Constants;
import org.lijingpeng.algo.cpm_predict.utils.InputArgsParser;

import java.io.IOException;

public class CpmForecastDriver {
    private static Log log = LogFactory.getLog(CpmForecastDriver.class);

    public static void main(String[] args) 
        throws OdpsException, IOException, InterruptedException {

        log.info("start to forecast cpms...");

        InputArgsParser.parseArgs(args);

        String projectName     = InputArgsParser
            .getArgValue(Constants.ARG_KEY_PROJECT_NAME).trim();

        String inputCpmTsTable = InputArgsParser
            .getArgValue(Constants.ARG_KEY_IN_CPM_SERIES).trim();

        String bizdate         = InputArgsParser
            .getArgValue(Constants.ARG_KEY_BIZDATE).trim();

        String outputTableName = InputArgsParser
            .getArgValue(Constants.ARG_KEY_OUT_TABLE).trim();

        JobConf job = new JobConf();
        job.setLong("odps.function.timeout", 1500);

        job.setMapperClass(CpmForecastMapper.class);
        job.setReducerClass(CpmForecastReducer.class);

        String mapOutKeySchema   = "member_id:bigint,type:bigint,sign:string,"
                                 + "adzone_id:string,quantile:bigint";

        String mapOutValueSchema = "dt:string,cpm:double";

        job.setMapOutputKeySchema(SchemaUtils.fromString(mapOutKeySchema));
        job.setMapOutputValueSchema(SchemaUtils.fromString(mapOutValueSchema));

        String[] sortedColumns = new String[] {"member_id", "type", "sign", 
                                               "adzone_id", "quantile"};

        String[] groupColumns  = new String[] {"member_id", "type", "sign", 
                                               "adzone_id", "quantile"};
        job.setOutputKeySortColumns(sortedColumns);
        job.setOutputGroupingColumns(groupColumns);

        // set up input table
        TableInfo inputTable = new TableInfo();
        inputTable.setProjectName(projectName);
        inputTable.setTableName(inputCpmTsTable);
        String tblPartStr = String.format(Constants.INPUT_TBL_PART_PTN, 
                bizdate);
        inputTable.setPartSpec(tblPartStr);
        InputUtils.addTable(inputTable, job);

        // set up output table
        TableInfo outputTable = new TableInfo();
        outputTable.setProjectName(projectName);
        outputTable.setTableName(outputTableName);
        String outTblPartStr = String.format(Constants.OUTPUT_TBL_PART_PTN, 
                bizdate);
        outputTable.setPartSpec(outTblPartStr);
        OutputUtils.addTable(outputTable, job);

        // run the job
        try {
            JobClient.runJob(job);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
