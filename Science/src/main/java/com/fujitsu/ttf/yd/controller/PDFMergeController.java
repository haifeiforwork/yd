package com.fujitsu.ttf.yd.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fujitsu.ttf.yd.entity.ThirdLevelDirectory;
import com.fujitsu.ttf.yd.service.PDFService;
import com.fujitsu.ttf.yd.service.ThirdLevelDirectoryService;
import com.fujitsu.ttf.yd.utils.Constants;
import com.fujitsu.ttf.yd.utils.TokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author 吴雨
 *
 */
@Api(value = "合并pdf并且发送", tags = "合并pdf并且发送")
@Controller
@RequestMapping("pDFMergeController")
public class PDFMergeController {
     
	@Autowired
	private PDFService pdfService;
	
	@Autowired
	private ThirdLevelDirectoryService thirdLevelDirectoryService;
	
	@PostMapping("mergerPdf")
	@ApiOperation(value = "合并pdf并且发送", notes = "")
	@ResponseBody
	@ApiImplicitParam(name = "Authorization", value = "Token", required = true, dataType = "String", paramType = "header")
	public void mergerPdf(String thirdNames,String targetAddress,String data,int id,String handbookName,HttpServletRequest request) {
		System.out.println("------------"+thirdNames+"-----------");
		String token = (String)request.getHeader(Constants.HEADER_AUTHORIZATION);
		String openid = TokenUtil.parse(token);
		System.out.println(openid);
		String compony="";
		String customer="";
		try {
			JSONObject json = new JSONObject(data);
			compony=json.getJSONObject("datemap").getString("compony");
			customer = json.getJSONObject("datemap").getString("customer");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(compony+"---------"+customer);
		//生成pdf封面
		pdfService.addMerge(data, "/root/cover.pdf",id);
		String[] split = thirdNames.split(",");
		//存放pdf
		List<String> list=new ArrayList<String>();

		for(String thirdName:split) {
			ThirdLevelDirectory directory = thirdLevelDirectoryService.selectDataByThirdName(thirdName);
			if(directory.getSecondId()==1) {
				list.add("/construction_machinery/industrial_rubber_hose/"+directory.getPdfName());
			}else if(directory.getSecondId()==2){
				list.add("/construction_machinery/hydraulic_hose/"+directory.getPdfName());
			// 空气弹簧
			}else if(directory.getSecondId()==8) {
				list.add("/air_suspension/pdf/"+directory.getPdfName());
			}else {
				list.add("/rail_transit/pdf/" + directory.getPdfName());
			}
		}
		// 去重
		for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )  {       
		      for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {       
		           if  (list.get(j).equals(list.get(i)))  {       
		              list.remove(j);       
		            }        
		        }        
		      }  
		System.err.println(list);
		pdfService.PDFMerge(list,targetAddress,handbookName,openid,compony,customer);
	}
}
