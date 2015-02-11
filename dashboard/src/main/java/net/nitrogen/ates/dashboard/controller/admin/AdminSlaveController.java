package net.nitrogen.ates.dashboard.controller.admin;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.SlaveModel;

public class AdminSlaveController extends Controller {
    public void index() {
        setAttr("slaveList", SlaveModel.me.findAllSlaves());
    }
}
