package payloads;


import java.time.Instant;
import java.util.List;
import java.util.Random;

import com.github.javafaker.Faker;

import pojo.Address;
import pojo.Cart;
import pojo.CartProduct;
import pojo.Geolocation;
import pojo.Name;
import pojo.Product;
import pojo.User;


public class Payload {

	private static final Faker faker=new Faker();
	private static final String categories[]= {"electronics", "furniture", "clothing", "books", "beauty"};
	
	private static final Random random=new Random();
	
		
	//Product
	public static Product productPayload()
	{
		String name=faker.commerce().productName();
		double price=Double.parseDouble(faker.commerce().price());
		String description=faker.lorem().sentence();
		String imageUrl="https://i.pravatar.cc/100";
		String category=categories[random.nextInt(categories.length)];
		
		new Product(name, price, description, imageUrl, category);
		return new Product(name, price, description, imageUrl, category);
	}
	
	public static User userPayload()
	{
		String firstname=faker.name().firstName();
		String lastname=faker.name().lastName();
		
		Name name=new Name(firstname,lastname);
		
		String lat=faker.address().latitude();
		String lng=faker.address().longitude();
		
		Geolocation location=new Geolocation(lat,lng);
		
		String city=faker.address().city();
		String street=faker.address().streetName();
		int number=random.nextInt(100);
		String zipcode=faker.address().zipCode();
		Address address=new Address(city,street,number,zipcode,location);
		
		String email=faker.internet().emailAddress();
		String username=faker.name().username();
		String password=faker.internet().password();
		String phonenumber=faker.phoneNumber().cellPhone();
		
		return new User(email,username,password,name,address,phonenumber);
	}
	
	
	public static Cart cartPayload(int userId)
	{
		String date=Instant.now().toString();
		int productId=random.nextInt(20)+1;
		int quantity=random.nextInt(5)+1;
		List<CartProduct> products=List.of(new CartProduct(productId, quantity));
		return new Cart(userId, date, products);
	}
	
	
	//User
	
	
	//Login
}
