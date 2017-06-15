package com.project.parking.data;

/**
 * Created by Yohanes on 15/06/2017.
 */

public class InqForgotPasswordResponse implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private MessageVO messageVO;

    public MessageVO getMessageVO() {
        return messageVO;
    }

    public void setMessageVO(MessageVO messageVO) {
        this.messageVO = messageVO;
    }
}
