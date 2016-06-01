package org.lijingpeng.algo.cpm_predict.utils;
public class Constants {
    public static final String PARAM_RESOURCE_NAME = "exp_smoothing_params";

    // table column definition, you may need to change this.
    public static final String INPUT_TBL_PART_PTN  = "ds=%s";
    public static final String OUTPUT_TBL_PART_PTN = "ds=%s";

    public static final String ARG_KEY_PROJECT_NAME   = "project_name";
    public static final String ARG_KEY_IN_CPM_SERIES  = "cpm_ts_table";
    public static final String ARG_KEY_BIZDATE        = "bizdate";
    public static final String ARG_KEY_OUT_TABLE      = "output_table";
    public static final String ARG_VALID_DATE_FORMAT  = "yyyyMMdd";
    public static final int    ARG_VALID_NUM          = 4;

    public static final String TBL_CPM_FIELD_MEMBER_ID = "member_id";
    public static final String TBL_CPM_FIELD_TYPE      = "type";
    public static final String TBL_CPM_FIELD_SIGN      = "sign";
    public static final String TBL_CPM_FIELD_ADZONEID  = "adzone_id";
    public static final String TBL_CPM_FIELD_QUANTILE  = "quantile";
    public static final String TBL_CPM_FIELD_CPM       = "cpm";
    public static final String TBL_CPM_FIELD_DT        = "dt";

    public static final String TBL_OUT_FIELD_MEMBER_ID = "member_id";
    public static final String TBL_OUT_FIELD_TYPE      = "type";
    public static final String TBL_OUT_FIELD_SIGN      = "sign";
    public static final String TBL_OUT_FIELD_ADZONEID  = "adzone_id";
    public static final String TBL_OUT_FIELD_QUANTILE  = "quantile";
    public static final String TBL_OUT_FIELD_CPM       = "cpm";
    public static final String TBL_OUT_FIELD_CPM_INTVL = "pred_intvl_95";

    public static final double DOUBLE_EXP_SMTH_INIT_ALPHA = 0.3;
    public static final double DOUBLE_EXP_SMTH_INIT_BETA  = 0.1;
    public static final int    DOUBLE_EXP_SMTH_MAX_ROUNDS = 100;
    public static final double DOUBLE_EXP_SMTH_CONV_THRES = 1.0E-4;
}
