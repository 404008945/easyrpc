package com.lxs.easyrpc.proxy.exporter;

import com.lxs.easyrpc.proxy.Invoke;
import com.lxs.easyrpc.proxy.impl.ProdInvoke;
import com.lxs.easyrpc.proxy.url.URL;

import java.util.Arrays;

public  class ExporterFacory {

  private static  ExporterManger exporterManger;

  private  ExporterFacory(){

  }

  public  static  ExporterManger getInstance(){
      if(exporterManger==null)
      {
          exporterManger = new ExporterManger();
      }
      return exporterManger;
  }

  public static void export(Invoke invoke){  //暴露服务
     //先转成exporter
      ProdInvoke prodInvoke = (ProdInvoke)invoke;
      if(prodInvoke.getMethods()!=null){
          Arrays.asList(prodInvoke.getMethods()).stream().forEach(e->{
              Exporter exporter = new Exporter(invoke,e);
              URL url = new URL(e);
              getInstance().export(url,exporter);
          });
      }


  }

}
