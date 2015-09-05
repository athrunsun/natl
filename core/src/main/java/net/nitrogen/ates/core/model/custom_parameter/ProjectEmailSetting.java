package net.nitrogen.ates.core.model.custom_parameter;

import java.util.List;

public class ProjectEmailSetting {

    private boolean isEmailEnabled;
    private boolean sendWhenExecutionStarted;
    private boolean sendWhenExecutionFinished;
    private String defaultRecipients;

    public class Keys {
        public static final String EMAIL_ENABLED = "email_enabled";
        public static final String SEND_WHEN_EXECUTION_STARTED = "execution_started";
        public static final String SEND_WHEN_EXECUTION_FINISHED = "execution_finished";
        public static final String DEFAULT_RECIPIENTS = "default_recipients";
    }

    public ProjectEmailSetting(boolean isEmailEnabled, boolean sendWhenExecutionStarted, boolean sendWhenExecutionFinished, String defaultRecipients) {
        this.isEmailEnabled = isEmailEnabled;
        this.sendWhenExecutionStarted = sendWhenExecutionStarted;
        this.sendWhenExecutionFinished = sendWhenExecutionFinished;
        this.defaultRecipients = defaultRecipients;
    }

    public ProjectEmailSetting(List<CustomParameterModel> parameterModels) {
        if (parameterModels == null || parameterModels.size() == 0) {
            this.setEmailEnabled(false);
            this.setSendWhenExecutionStarted(false);
            this.setSendWhenExecutionFinished(false);
            this.setDefaultRecipients("");
        }
        for (CustomParameterModel model : parameterModels) {
            if (model.getKey().equals(Keys.EMAIL_ENABLED)) {
                this.setEmailEnabled(model.getValue().equals("1") || model.getValue().equalsIgnoreCase("true"));
                continue;
            }
            if (model.getKey().equals(Keys.SEND_WHEN_EXECUTION_STARTED)) {
                this.setSendWhenExecutionStarted(model.getValue().equals("1") || model.getValue().equalsIgnoreCase("true"));
                continue;
            }
            if (model.getKey().equals(Keys.SEND_WHEN_EXECUTION_FINISHED)) {
                this.setSendWhenExecutionFinished(model.getValue().equals("1") || model.getValue().equalsIgnoreCase("true"));
                continue;
            }
            if (model.getKey().equals(Keys.DEFAULT_RECIPIENTS)) {
                this.setDefaultRecipients(model.getValue());
                continue;
            }
        }
    }

    /**
     * @return the isEmailEnabled
     */
    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    /**
     * @param isEmailEnabled
     *            the isEmailEnabled to set
     */
    public void setEmailEnabled(boolean isEmailEnabled) {
        this.isEmailEnabled = isEmailEnabled;
    }

    /**
     * @return the sendWhenExecutionStarted
     */
    public boolean isSendWhenExecutionStarted() {
        return sendWhenExecutionStarted;
    }

    /**
     * @param sendWhenExecutionStarted
     *            the sendWhenExecutionStarted to set
     */
    public void setSendWhenExecutionStarted(boolean sendWhenExecutionStarted) {
        this.sendWhenExecutionStarted = sendWhenExecutionStarted;
    }

    /**
     * @return the sendWhenExecutionFinished
     */
    public boolean isSendWhenExecutionFinished() {
        return sendWhenExecutionFinished;
    }

    /**
     * @param sendWhenExecutionFinished
     *            the sendWhenExecutionFinished to set
     */
    public void setSendWhenExecutionFinished(boolean sendWhenExecutionFinished) {
        this.sendWhenExecutionFinished = sendWhenExecutionFinished;
    }

    /**
     * @return the defaultRecipients
     */
    public String getDefaultRecipients() {
        return defaultRecipients;
    }

    /**
     * @param defaultRecipients
     *            the defaultRecipients to set
     */
    public void setDefaultRecipients(String defaultRecipients) {
        this.defaultRecipients = defaultRecipients;
    }

}
