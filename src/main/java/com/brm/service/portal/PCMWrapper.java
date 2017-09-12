package com.brm.service.portal;

import org.apache.log4j.Logger;

import com.brm.service.portal.utils.BRMException;
import com.brm.service.portal.utils.ErrorCodes;
import com.portal.pcm.*;
import com.portal.pcm.fields.*;

public class PCMWrapper {
	private PortalContext context;
	private static boolean debug = false;
	private int transactionStatus; //1 for open, 2 for abort, 3 for commit
	private static final Logger log = Logger.getLogger("Connector");
	private long db = 1;
	private long id = -1;
	
	public PCMWrapper() {
		log.info("==================================================================================");
		log.debug("PCMWrapper Ver-" + ErrorCodes.PCMWRAPPER_VER );
		context = null;
	}
	
	public void enableDebugLoging() {
		debug = true;
		log.debug("Enable Debug Loging");
	}
	
	public long getDb() {
		return db;
	}

	public long getId() {
		return id;
	}

/**
 *  Method Name : createContext <p>
 *  Description : connect to portal <P>
 *  Revision Information:<p>
 *  Date            Author                   Tracking            Description<p>
 *  --------------- ------------------------ --------------------- ------------------------------------<p>
 *  08DEC2010       Anu Ponnappan            ${todo}               Initial draft.<BR>
 *
 * Input : 
 * Output: 
 */            	
	public void createContext() throws BRMException  {
		log.debug("Create context called.");		 
		try {
			context = new PortalContext();
			context.connect();
			log.debug("Connected to PORTAL.");
			String debugModeOn = PortalContext.getUserProperty("pcmwraper.isDebugLog");
			if (debugModeOn != null && debugModeOn.equalsIgnoreCase("true")) {
				enableDebugLoging();
			}
		} catch (Exception ex) {
			log.fatal("PORTAL connect failed.");
			throw new BRMException(ex.getMessage(), ErrorCodes.ERR_PCM_CONNECT_FAILED, ex);
		}
	}
	 
	public void closeContext() throws BRMException {
		if (context != null) {
			try {
				context.close(true);
				log.debug("Closing PORTAL-PCM connection.");
			} catch (EBufException ex) {
				log.fatal("Closing PORTAL-PCM connection call failed.");
				throw new BRMException(ex.getMessage(), ErrorCodes.ERR_PCM_DISCONNECT_FAILED, ex);
			}catch (Exception ex) {
				log.fatal("Closing PORTAL-PCM connection call failed.");
				log.fatal(BRMException.getStackTraceAsString(ex));
				throw new BRMException(ex.getMessage(), ErrorCodes.ERR_PCM_DISCONNECT_FAILED, ex);
			}
		} else {
			log.fatal("Closing PORTAL-PCM connection call failed.");
			throw new BRMException("Closing PORTAL-PCM connection call failed", ErrorCodes.ERR_PCM_DISCONNECT_FAILED);
		}
				  
	}
	
	public PortalContext getContext() {
		if (context != null) {
			return context;
		}
		log.fatal("Not connected with PORTAL.");
		return null;
	}
	
	private void contextCheck(String caller) throws BRMException {
		if (context == null)
			throw new BRMException("PCMWrapper." + caller +
									" -> Portal Context is null.",
									ErrorCodes.ERR_PCM_CONNECT_FAILED);
	}
	
	private FList transaction(Poid poid, String value) throws BRMException {
		contextCheck(value + "Transaction");
		FList input = new FList();
		input.set(FldPoid.getInst(), poid);
		return input;
	}
	
	public FList openTransaction(Poid poid) throws BRMException {
		FList output = this.callOpcode(PortalOp.TRANS_OPEN, PortalContext.TRAN_OPEN_READWRITE, transaction(poid, "Open"), "OpenTransaction");
		this.transactionStatus = 1;
		return output;
	}

	public boolean isTransactionOpen() {
		if (this.transactionStatus == 1)
			return true;
		else
			return false;
	}

	public FList abortTransaction(Poid poid) throws BRMException {
		FList output = this.callOpcode(PortalOp.TRANS_ABORT, PortalContext.TRAN_OPEN_READWRITE, transaction(poid, "Abort"), "AbortTransaction");
		this.transactionStatus = 2;
		return output;
	}

	public FList commitTransaction(Poid poid) throws BRMException {
		FList output = this.callOpcode(PortalOp.TRANS_COMMIT, PortalContext.TRAN_OPEN_READWRITE, transaction(poid, "Commit"), "CommitTransaction");
		this.transactionStatus = 3;
		return output;
	}

	
	public FList callOpcode(int opcode, FList input, String calledFrom) throws BRMException{
		return callOpcode(opcode, -9000, input, calledFrom);
	}
	
	public FList callOpcode(int opcode, int opcodeFlags, FList input, String calledFrom) throws BRMException {
		contextCheck("callOpcode->" + calledFrom + "[OPCODE:" + opcode + "]");
		FList output = null;
		if (input == null)
			throw new BRMException("Input FList is null", ErrorCodes.ERR_FLIST_NULL);
		
		try {
			stdOut(input, "input: " + calledFrom + " [OPCODE: " + opcode + "]");
			if (opcodeFlags == -9000)
				output = context.opcode(opcode, input);
			else
				output = context.opcode(opcode, opcodeFlags, input);
			stdOut(output, "output: " + calledFrom + " [OPCODE: " + opcode + "]");
			if (output != null) {
				return output;				
			}
			throw new BRMException("Output FList (" + calledFrom + " ) is null/empty.", ErrorCodes.ERR_FLIST_NULL); 
		} catch (EBufException ex) {
			
			if (opcode == PortalOp.TRANS_OPEN)
				throw new BRMException("Opcode call (" + calledFrom + ") failed.",
			                                  ErrorCodes.ERR_PCM_TRANSACTION_OPEN_FAILED, ex);
			else if (opcode == PortalOp.TRANS_ABORT)
				throw new BRMException("Opcode call (" + calledFrom + ") failed.",
			                                  ErrorCodes.ERR_PCM_TRANSACTION_ABORT_FAILED, ex);
			else if (opcode == PortalOp.TRANS_COMMIT)
				throw new BRMException("Opcode call (" + calledFrom + ") failed.",
			                                  ErrorCodes.ERR_PCM_TRANSACTION_COMMIT_FAILED, ex);
			else if (ex.getErrorString().equals("ERR_CREDIT_LIMIT_EXCEEDED")) {
				throw new BRMException("Credit limit exceeded", ErrorCodes.ERR_CREDIT_LIMIT_EXCEEDED);
			}
			else {
				stdOut(BRMException.getStackTraceAsString(ex),
			                    "OPCODE ERROR, " + calledFrom);
				throw new BRMException("Opcode call (" + calledFrom + ") failed. " +
			                                  "Error Message is " + ex.getErrorString() +
			                                  ". Error Field is " + ex.getFieldString() +
			                                  ". Error Location is " + ex.getLocationString(),
			                                  ErrorCodes.ERR_UNKNOWN_ERROR, ex);
			}
		}

	}
	
	public static void stdOut(FList fList, String name) {
		if (debug) {
			log.debug(name + " { " + fList + "}");
		}
	}
	
	public static void stdOut(String out, String name) {
		if (debug) {
			log.debug(name + " { " + out + "}");
		}
	}
	
	public FList getAccountByAccountNo(String acctNo) throws BRMException {

		contextCheck("getAccountByAccountNo");
		
		//set account no
		FList flistAcctNo = new FList();
		flistAcctNo.set(FldAccountNo.getInst(), acctNo);
	
		//prepare input flist
		FList inFlist = new FList();
		FList resultFlist = new FList();
		inFlist.setElement(FldArgs.getInst(), 1, flistAcctNo); //set element in args array
		inFlist.set(FldPoid.getInst(), new Poid(db, id, "/search"));
		inFlist.set(FldTemplate.getInst(),
							  " select X from /account where F1 = V1 ");
		inFlist.set(FldFlags.getInst(), 256); //Search Distinct
		inFlist.setElement(FldResults.getInst(), 1, resultFlist); //set for result set
	
		FList outFlist = null;
		outFlist = this.callOpcode(PortalOp.SEARCH, inFlist,
											 "getAccountByAccountNo");
		try {
			if (outFlist.hasField(FldResults.getInst()))  //result set is empty or not
				return outFlist.getElement(FldResults.getInst(), 0); //read the 0th element
			  //POID not found
			  throw new BRMException(
				  "This account is not created yet. Account POID not found.",
				  ErrorCodes.ERR_ACCOUNT_POID_NOT_FOUND);
		}catch (EBufException ex) {
			throw new BRMException(
			  "Account search failed for the given account no. FList parse error.",
			  ErrorCodes.ERR_FLIST_PARSE_ERR, ex);
		}
	}
	
	public FList getBillinfoFromAccount(Poid acctPoid) throws BRMException {

		contextCheck("getBillinfoFromAccount");

		//set account no
		FList argsFlist = new FList();
		argsFlist.set(FldAccountObj.getInst(), acctPoid);
	
		//prepare input flist
		FList inFlist = new FList();
		FList resultFlist = new FList();
		inFlist.setElement(FldArgs.getInst(), 1, argsFlist); //set element in args array
		inFlist.set(FldPoid.getInst(), new Poid(db, id, "/search"));
		inFlist.set(FldTemplate.getInst(),
							  " select X from /billinfo where F1 = V1 ");
		inFlist.set(FldFlags.getInst(), 256); //Search Distinct
		inFlist.setElement(FldResults.getInst(), 1, resultFlist); //set for result set
	
		FList outFlist = null;
		outFlist = this.callOpcode(PortalOp.SEARCH, inFlist,
											 "getBillinfoFromAccount");
		try {
			if (outFlist.hasField(FldResults.getInst()))  //result set is empty or not
				return outFlist.getElement(FldResults.getInst(), 0); //read the 0th element
			  //POID not found
			  throw new BRMException(
				  "Billinfo POID not found.",
				  ErrorCodes.POID_NOT_FOUND);
		}catch (EBufException ex) {
			throw new BRMException(
			  "Billinfo search failed for the given account poid. FList parse error.",
			  ErrorCodes.ERR_FLIST_PARSE_ERR, ex);
		}
	}
	
	public FList search(FList inFlist, int flag, String searchFor) throws BRMException {
		
		inFlist.set(FldFlags.getInst(), flag);
		inFlist.set(FldPoid.getInst(), new Poid(db, id, "/search"));
		FList outFlist = this.callOpcode(PortalOp.SEARCH, inFlist, searchFor);
		if (outFlist != null && outFlist.hasField(FldResults.getInst())) {
			return outFlist;
		 }

		throw new BRMException("Search failed, output is without results. Called from " 
				+ searchFor, ErrorCodes.ERR_EMPTY_RESULTS_IN_SEARCH_MEANS_VALUE_NOT_FOUND);
	}
	
	public FList searchNoException(FList inFlist, int flag, String searchFor) throws BRMException {
		
		inFlist.set(FldFlags.getInst(), flag);
		inFlist.set(FldPoid.getInst(), new Poid(db, id, "/search"));
		FList outFlist = this.callOpcode(PortalOp.SEARCH, inFlist, searchFor);
		if (outFlist != null && outFlist.hasField(FldResults.getInst())) {
			return outFlist;
		 }

		return null;
	}
	
	public Poid getServicePoidFromType(Poid acctPoid, String serviceType) throws BRMException {

		contextCheck("getServicePoidFromType");

		//prepare input flist
		FList inFlist = new FList();
		FList resultFlist = new FList();
		resultFlist.set(FldPoid.getInst());
		
		//set account no
		FList argsFlist = new FList();
		argsFlist.set(FldAccountObj.getInst(), acctPoid);
		inFlist.setElement(FldArgs.getInst(), 1, argsFlist); //set element in args array
		argsFlist = new FList();
		argsFlist.set(FldPoid.getInst(), new Poid(db, id, serviceType));
		inFlist.setElement(FldArgs.getInst(), 2, argsFlist);
		
		inFlist.set(FldPoid.getInst(), new Poid(db, id, "/search"));
		inFlist.set(FldTemplate.getInst(),
							  " select X from /service/$1 where F1 = V1 and F2 = V2 ");
		inFlist.set(FldFlags.getInst(), 256); //Search Distinct
		inFlist.setElement(FldResults.getInst(), 1, resultFlist); //set for result set
	
		FList outFlist = null;
		outFlist = this.callOpcode(PortalOp.SEARCH, inFlist,
											 "getServicePoidFromType");
		try {
			if (outFlist.hasField(FldResults.getInst())) { //result set is empty or not
				FList retFlist = outFlist.getElement(FldResults.getInst(), 0);
				return retFlist.get(FldPoid.getInst()); //read the 0th element
			}
			  //POID not found
			  throw new BRMException(
				  "Service POID not found for ." +serviceType,
				  ErrorCodes.POID_NOT_FOUND);
		}catch (EBufException ex) {
			throw new BRMException(
			  "ServicePoid search failed for the given account poid and serviceType. FList parse error.",
			  ErrorCodes.ERR_FLIST_PARSE_ERR, ex);
		}
	}
	
	public Poid getAccountPoidFromAccountNo(String accountNo) throws BRMException, EBufException {
		
		FlistCreator creator = new FlistCreator();
		FList srchInFlist = creator.getPoidFromStrField("/account", FldAccountNo.getInst(), accountNo);
		
		FList srchOutFlist = this.search(srchInFlist, 256, "Search Poid by Account No");
		FList acctFlist = srchOutFlist.getElement(FldResults.getInst(), 0);
		return acctFlist.get(FldPoid.getInst());
	}
	
	public FList getCustomerPayinfo(Poid accountPoid, String typeStr, int OpcodeFlags) throws BRMException {
		
		FList inputFlist = new FList();
		inputFlist.set(FldPoid.getInst(), accountPoid);
		inputFlist.set(FldAccountObj.getInst(), accountPoid);
		if(typeStr != null)
			inputFlist.set(FldTypeStr.getInst(), typeStr);
		FList returnFList = this.callOpcode(PortalOp.CUST_FIND_PAYINFO,	OpcodeFlags,
				 			inputFlist, "PCMWrapper - getCustomerPayinfo");
		return returnFList;
	}
	
	public FList getAccountPayments(Poid accountPoid, int count, int status, String poidType) throws BRMException  {
		
		FList inputFlist = new FList();
		inputFlist.set(FldPoid.getInst(), accountPoid);
		inputFlist.set(FldStatus.getInst(), status);
		inputFlist.set(FldIncludeChildren.getInst(), 0);
		inputFlist.set(FldThreshold.getInst(), count);
		inputFlist.set(FldAmountIndicator.getInst(), 0);
		inputFlist.set(FldPoidType.getInst(), poidType);
		
		return this.callOpcode(PortalOp.AR_GET_ACCT_ACTION_ITEMS, inputFlist, "getAccountPayments");
		
	}
	
	public FList arGetItemDetails(Poid itemPoid) throws BRMException  {
		
		FList inputFlist = new FList();
		inputFlist.set(FldPoid.getInst(), itemPoid);
		return this.callOpcode(PortalOp.AR_GET_ITEM_DETAIL, inputFlist, "arGetItemDetails");
	}
	
	//Arindam
	public FList getDealDetails(Poid dealObj) throws BRMException {
		
		FList inputFlist = new FList();
		FList products = new FList();
		inputFlist.set(FldPoid.getInst(), dealObj);
		inputFlist.setElement(FldProducts.getInst(), 1, products); //set for PRODUCTS array which will be populated in response
		inputFlist.setElement(FldDiscounts.getInst(), 1, products); //set for DISCOUNTS array which will be populated in response
			
		FList returnFList = this.callOpcode(PortalOp.READ_OBJ,
				 			inputFlist, "PCMWrapper - getCustomerPayinfo");
		return returnFList;
	}
}
