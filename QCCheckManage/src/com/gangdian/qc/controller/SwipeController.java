package com.gangdian.qc.controller;

import java.util.List;
import java.util.Map;



import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gangdian.qc.model.ProductMain;
import com.gangdian.qc.model.QCCheckProject;
import com.gangdian.qc.service.QCCheckProjectService;
import com.gangdian.qc.service.SwipeServer;
import com.springmvc.common.PrintLable;
import com.springmvc.sys.ZUtil;
import com.springmvc.util.Const;
import com.springmvc.util.PrintQRCODE;


@Controller
@RequestMapping("/qc")
public class SwipeController {

	@Autowired
	private SwipeServer swipeServer;
	@Autowired
	private QCCheckProjectService checkProjectService;
	private  Logger log=Logger.getLogger(this.getClass());
	private  PrintQRCODE printer=new PrintQRCODE();
	//��ȡid������Ϣ
	@RequestMapping("getCardInfo.do")
	@ResponseBody
	public List<Map<String, Object>> getCardInfo(){
		return swipeServer.getAllCard();
	}
	
	//��ȡ����붩���İ���Ϣ
	@RequestMapping("getBindPono.do")
	@ResponseBody
	public List<Map<String, Object>> getBindPono(){
		return swipeServer.getAllMachine();
	}
	
	//��ȡȫ��ˢ������Ϣ
	@RequestMapping("getSwipeData.do")
	@ResponseBody
	public List<Map<String, Object>> getSwipeData(){
		return swipeServer.getAllData();
	}
	
	//ɸѡˢ����Ϣ
	@RequestMapping("getSwipeDataByParams.do")
	@ResponseBody
	public Map<String, Object> getSwipeDataByParams(String pono,
			Integer groupid, Integer position, String swipedate,String serialnum,
			Integer rows,Integer page, String sort, String order){
		return swipeServer.getDataByCondition(pono, groupid, position, swipedate, serialnum, rows, page, sort, order);
	}
	
	
	//����һ��product_mian,����ˢ�����Ͷ�����
	@RequestMapping("startQCPPandBindpono.do")
	@ResponseBody
	public Integer startQCPPandBindpono(ProductMain pm){
		return swipeServer.startQCPPandBindpono(pm);
	}
	
	//����product_mian�Ľ���ʱ��,�����ˢ�����Ͷ����İ�
	@RequestMapping("endQCPPandUnbindpono.do")
	@ResponseBody
	public boolean endQCPPandUnbindpono(ProductMain pm){
		return swipeServer.endQCPPandUnbindpono(pm);
	}
	
	//��¶������ķ���,û�й���
	@RequestMapping("loginprint.do")
	@ResponseBody
	public String printest(String cserialnum,String printname,String code){
		//��ֹ������ʴ�ӡ��������һ��code,������������code������
		if(code==null||!code.equals("123456")||printname==null||cserialnum==null){
			return "-1";
		}
		try {
			printer.print(cserialnum);
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "1";
		}		
	}
	
	//���Դ�ӡ�ķ���
	@RequestMapping("print.do")
	@ResponseBody
	public String print(String cserialnum,String printname){
		String result="false";
		if(cserialnum==null||cserialnum.trim().equals("")){
			cserialnum="��ӡ����";
		}
		System.out.println(cserialnum);
		System.out.println(printname);
		try {
			printer.print(cserialnum);
			result="true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	//��������ɾ��,�ظ����
	@RequestMapping("addSMachine.do")
	@ResponseBody
	public boolean addSMachine(Integer mgid,Integer mgroupid,String mgroupname,
			String mpono,String msequence,String mprintname){
		return swipeServer.addMachine(mgid, mgroupid, mgroupname, mpono, msequence, mprintname);
	}
	@RequestMapping("updateSMachine.do")
	@ResponseBody
	public boolean updateSMachine(Integer mgid,Integer mgroupid,String mgroupname,
			String mpono,String msequence,String mprintname){
		return swipeServer.updateMachine(mgid, mgroupid, mgroupname, mpono, msequence, mprintname);
	}
	@RequestMapping("deleteSMachine.do")
	@ResponseBody
	public boolean deleteSMachine(Integer mgid){
		return swipeServer.deleteMachineByGid(mgid);
	}
	@RequestMapping("checkMgid.do")
	@ResponseBody
	public boolean checkMgid(int mgid){
		return !swipeServer.checkMgid(mgid);
	}
	
	
	/**
	 * ��������Ϣ
	 * @param serialnum ��������
	 * @param pono  ������
	 * @param groupname �������
	 * @param qcid  �����Ŀ����
	 * @param groupid ���id
	 * @return
	 */
	@RequestMapping("productCheck.do")
	@ResponseBody
	public String productCheck(String serialnum,String pono,
			String groupname,String qcid,
			Integer groupid){
		
		log.info("insert  check postion  pono :"+pono+" groupname:"+groupname+" qcid"+qcid);
		Integer position=Const.CHECKPOSITION;
		
		String ispono=swipeServer.checkSerialNum(position, serialnum);
		//�Ƿ��Ѿ�ˢ��
		if(!StringUtils.isEmpty(ispono)){
			return "fail03";
		}
		//��鶩�����źͶ������Ƿ�һ��
		if(!serialnum.contains(ZUtil.replaceP(pono))){
			return "fail01";
		}
		//����Ƿ���ڶ�Ӧ����Ŀ����
		QCCheckProject qcProject = this.checkProjectService.selectByPrimaryKey(qcid);
		if(qcProject==null){
			return "fail02";
		}
		try {
			swipeServer.addCheckData(serialnum, groupid, groupname, pono, position, qcid);
			return "success";
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}
	@RequestMapping("checkData.do")
	@ResponseBody
	public  List<Map<String, Object>> checkData(Integer groupid){
			return swipeServer.getCheckNum(groupid, Const.CHECKPOSITION);
	}
	
	
	@RequestMapping("getFailureBySerialnum.do")
	@ResponseBody
	public List<Map<String, Object>> getFailureBySerialnum(String serialnum) {
		return swipeServer.getFailureBySerialnum(serialnum);
	}

	@RequestMapping("addRepairRecord.do")
	@ResponseBody
	public boolean addRepairRecord(String serialnum, String badcode,
			String result, String description, String repairman) {
		return swipeServer.addRepairRecord(serialnum, badcode, result, description, repairman);
	}

	@RequestMapping("getRepairRecordByManAndDate.do")
	@ResponseBody
	public List<Map<String, Object>> getRepairRecordByManAndDate(String serialnum,
			String repairman, String repairdate) {
		return swipeServer.getRepairRecordByManAndDate(serialnum,repairman,repairdate);
	}

	@RequestMapping("getRepairRecordNum.do")
	@ResponseBody
	public int getRepairRecordNum(String repairman, String repairdate) {
		return swipeServer.getRepairRecordNum(repairman, repairdate);
	}
	
	
	//�����ձ�v2
	//QC�������
	@RequestMapping("getQCCheckDetail.do")
	@ResponseBody
	public Map<String, Object> getQCCheckDetail(String qcid,String pono,String swipedate,
			Integer rows,Integer page,String sort,String order) {
		return swipeServer.getQCCheckDetail(qcid, pono, swipedate, rows, page, sort, order);
	}
	//��λɨ������
	@RequestMapping("getStationCount.do")
	@ResponseBody
	public List<Map<String, Object>> getStationCount(Integer groupid) {
		return swipeServer.getStationCount(groupid);
	}
	
}