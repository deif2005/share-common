package com.wd.commonpo;

/**
 * Created by defi on 2016/7/7.
 * 用途；用来对应取值序号常量值，以便直观了解取值的具体内容
 */
public enum EnumOrdinal {
    //对应status状态：1正常，2删除，3停用或其它'
    normal("1"), delete("2"), other("3"){
        @Override
        public boolean isRest() {
            return true;
        }
    },
    //对应题型导入excel表的列名:1题型名称，2题型备注（主客观类型）
    QuestionTypeName("1"),QuestionTypeRemark("2") {
        @Override
        public boolean isRest() {
            return true;
        }
    },
    //场次考试状态 1.未开考，2允许登陆，3考试中，4禁止登陆，5考试完成，6答案已审核，7答案已导出
    unExamine("1"),startLogin("2"),inExamine("3"),endLogin("4"),
    endExamine("5"),checkedAnswer("6"),exportedAnswer("7"){
        @Override
        public boolean isRest() {return true; }
    },
    //考生答案状态 1未上传、2上传中、3未校验、4校验失败、5校验成功、6已导出
    unUploadAns("1"), AnsUploading("2"), unCheckAns("3"),
    checkAnsFail("4"), checkAnsSuccess("5"), exportedAns("6"){
        @Override
        public boolean isRest(){return true;}
    },
    //考生考试状态，1.未登入，2已登入，3环境监测中，4等待开考，5答题中，
    // 6本机续考，7移机续考，8交卷中，9正常完成，10异常完成，11下场重考，12考生弃考'
    unlogin("1"), logined("2"), envChecking("3"), waitExamine("4"),
    inTheAnswer("5"), localContinue("6"), moveContinue("7"), handedPaper("8"),
    finishExamine("9"), exceptExamine("10"), nextReexamine("11"), abandonExamine("12"){
       @Override
       public boolean isRest(){return true;}
    },
    //客户端锁定状态:1，锁定；2，未锁定
    locked("1"), unlock("2"){
        @Override
        public boolean isRest(){return true;}
    };


    private String value;

    private EnumOrdinal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isRest() {
        return false;
    }
}

