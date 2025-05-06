package api;

public class SubscriberData {
    private String msisdn;
    private Double balance;
    private Integer tariffId;

    public SubscriberData() {
    }

    public SubscriberData(String msisdn, Double balance, Integer tariffId) {
        this.msisdn = msisdn;
        this.balance = balance;
        this.tariffId = tariffId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getTariffId() {
        return tariffId;
    }

    public void setTariffId(Integer tariffId) {
        this.tariffId = tariffId;
    }
}
