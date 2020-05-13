package com.lxs.easyregister.manager;

public class ManagerFactory {

    private static RegisterManager manager;

   public static   RegisterManager getInstance(){

       if(manager==null)
       {
            manager = new RegisterManager();
       }
       return manager;
    }
}
