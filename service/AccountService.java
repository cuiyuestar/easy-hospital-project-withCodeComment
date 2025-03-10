package com.easy.hospital.service;

import com.easy.hospital.model.bo.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@NoArgsConstructor
@Data
public class AccountService {
  public static final String ABI = com.easy.hospital.utils.IOUtil.readResourceAsString("abi/Account.abi");

  public static final String BINARY = com.easy.hospital.utils.IOUtil.readResourceAsString("bin/ecc/Account.bin");

  public static final String SM_BINARY = com.easy.hospital.utils.IOUtil.readResourceAsString("bin/sm/Account.bin");

  @Value("${system.contract.accountAddress}")
  private String address;

  @Autowired
  private Client client;

  AssembleTransactionProcessor txProcessor;

  @PostConstruct
  public void init() throws Exception {
    this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.client.getCryptoSuite().getCryptoKeyPair());
  }

  public CallResponse getUsT() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getUsT", Arrays.asList());
  }

  public CallResponse getT(AccountGetTInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getT", input.toArgs());
  }

  public TransactionResponse addDoctor(AccountAddDoctorInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "addDoctor", input.toArgs());
  }

  public CallResponse isRejected(AccountIsRejectedInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "isRejected", input.toArgs());
  }

  public CallResponse getV(AccountGetVInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getV", input.toArgs());
  }

  public TransactionResponse addRejectedPatient(AccountAddRejectedPatientInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "addRejectedPatient", input.toArgs());
  }

  public CallResponse getDoctorScore(AccountGetDoctorScoreInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getDoctorScore", input.toArgs());
  }

  public TransactionResponse vote(AccountVoteInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "vote", input.toArgs());
  }

  public CallResponse isAccountGenerated(AccountIsAccountGeneratedInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "isAccountGenerated", input.toArgs());
  }

  public CallResponse patients(AccountPatientsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "patients", input.toArgs());
  }

  public TransactionResponse pay(AccountPayInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "pay", input.toArgs());
  }

  public CallResponse doctorIdToIndex(AccountDoctorIdToIndexInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "doctorIdToIndex", input.toArgs());
  }

  public CallResponse patientIdToIndex(AccountPatientIdToIndexInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "patientIdToIndex", input.toArgs());
  }

  public CallResponse doctors(AccountDoctorsInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "doctors", input.toArgs());
  }

  public TransactionResponse tTrans(AccountTTransInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "tTrans", input.toArgs());
  }

  public TransactionResponse addPatient(AccountAddPatientInputBO input) throws Exception {
    return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, "addPatient", input.toArgs());
  }

  public CallResponse getCapitalPoolT() throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "getCapitalPoolT", Arrays.asList());
  }

  public CallResponse reEvaId(AccountReEvaIdInputBO input) throws Exception {
    return this.txProcessor.sendCall(this.client.getCryptoSuite().getCryptoKeyPair().getAddress(), this.address, ABI, "reEvaId", input.toArgs());
  }
}
