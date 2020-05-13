package com.lxs.remote;

import com.lxs.remote.adapter.ResponseMessageContentAdapter;

public class RegisterResponseMessageContent extends ResponseMessageContentAdapter<RegisterResult> {

    private RegisterResult result;


    @Override
    public RegisterResult getResult() {
        return result;
    }

    public void setResult(RegisterResult result) {
        this.result = result;
    }

}
