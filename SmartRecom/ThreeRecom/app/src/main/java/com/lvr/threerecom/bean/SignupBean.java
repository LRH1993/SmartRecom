package com.lvr.threerecom.bean;

/**
 * Created by lvr on 2017/5/19.
 */

public class SignupBean {

    /**
     * register_prompt_info : 嘻嘻，注册成功！
     * login_state : null
     */

    private String register_prompt_info;
    private String login_state;

    public String getRegister_prompt_info() {
        return register_prompt_info;
    }

    public void setRegister_prompt_info(String register_prompt_info) {
        this.register_prompt_info = register_prompt_info;
    }

    public String getLogin_state() {
        return login_state;
    }

    public void setLogin_state(String login_state) {
        this.login_state = login_state;
    }
}
