package org.hadatac.console.controllers.deployments;

import play.mvc.Controller;
import play.mvc.Result;
import org.hadatac.console.views.html.deployments.*;
import org.hadatac.console.controllers.deployments.*;


public class DeleteDeployment extends Controller {
	
	// for /metadata HTTP GET requests
    public static Result index() {

       System.out.println("deleting deployment");
       return redirect(routes.DeploymentManagement.index());
        
    }// /index()


    // for /metadata HTTP POST requests
    public static Result postIndex() {
        
        System.out.println("deleting deployment");
        return redirect(routes.DeploymentManagement.index());
        
    }// /postIndex()

}
