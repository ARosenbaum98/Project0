package org.project.beans;

/*
id
user_id
loan_amount
interest_rate
pending_approval
approved
 */

import java.text.DecimalFormat;
import java.util.Objects;

public class Credit {

    private int id;
    private int userId;
    private double loanAmount;
    private double interestRate;
    private boolean pending_approval;
    private boolean approved;



    public Credit(int id, int userId, double loanAmount, double interestRate, boolean pending_approval, boolean approved) {
        this.id = id;
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.pending_approval = pending_approval;
        this.approved = approved;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getLoanAmount() {

        final DecimalFormat dollarFormat = new DecimalFormat("#.##");
        return Double.parseDouble(dollarFormat.format(loanAmount));
    }

    public double getInterestRate() {
        final DecimalFormat percentFormat = new DecimalFormat("#.######");
        return Double.parseDouble(percentFormat.format(interestRate));
    }

    public boolean isPending_approval() {
        return pending_approval;
    }

    public boolean isApproved() {
        return approved;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", userId=" + userId +
                ", loanAmount=" + loanAmount +
                ", interestRate=" + interestRate +
                ", pending_approval=" + pending_approval +
                ", approved=" + approved +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credit credit = (Credit) o;
        return id == credit.id && userId == credit.userId && Double.compare(credit.loanAmount, loanAmount) == 0 && Double.compare(credit.interestRate, interestRate) == 0 && pending_approval == credit.pending_approval && approved == credit.approved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, loanAmount, interestRate, pending_approval, approved);
    }
}