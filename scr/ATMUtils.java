package scr;

import java.text.NumberFormat;
import java.util.Locale;

public class ATMUtils {
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public static String formatCurrency(double amount) {
        return currencyFormatter.format(amount);
    }

    // You can add more utility methods here as needed
}