package hello;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.smartcardio.CardException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.model.Charge;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public static void chargeCreditCard(String apiKey, String customerToken, BigInteger amount, String description) {
		
		Stripe.apiKey = apiKey;
		// Create the charge on Stripe's servers - this will charge the user's card
		try {
		  Map<String, Object> chargeParams = new HashMap<String, Object>();
		  chargeParams.put("amount", amount.intValue()); // amount in cents, again
		  chargeParams.put("currency", "usd");
		  chargeParams.put("source", customerToken);
		  chargeParams.put("description", "Example charge");

		  Charge charge = Charge.create(chargeParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

    @RequestMapping("/greeting")
    public Greeting greeting(HttpServletRequest request, @RequestParam(value="name", defaultValue="World") String name) {
    	String token = request.getParameter("stripeToken");
		System.out.println("Stripe "+token);
		chargeCreditCard( "sk_test_wjbyr0gd6KZ1vpnwvdTB68Cu", token, new BigInteger("1200"), "Test transfer 2");
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
