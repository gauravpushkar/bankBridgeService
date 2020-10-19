package io.bankbridge.model;
import java.util.List;

/***
 * Model class for Banks Collection
 */
public class BankModelList {
	
	private List<BankModel> banks;

	public List<BankModel> getBanks() {
		return banks;
	}

	public void setBanks(List<BankModel> banks) {
		this.banks = banks;
	}
}
