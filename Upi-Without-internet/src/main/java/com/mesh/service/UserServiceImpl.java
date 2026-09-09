package com.mesh.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mesh.entity.Account;
import com.mesh.entity.User;
import com.mesh.repository.AccountRepository;
import com.mesh.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	
	
	private UserRepository userRepository;
	private AccountRepository accountRepository;

	@Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AccountRepository accountRepository) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

	public User registerUser(String name, String phone, String password) {

	    System.out.println("REGISTER API HIT");

	    String vpa = generateVpa(phone);

	    User user = new User();
	    user.setName(name);
	    user.setPhone(phone);
	    user.setVpa(vpa);
	    user.setPassword(password);

	    user = userRepository.save(user);

	    System.out.println("USER SAVED: " + user.getId());

	    Account account = new Account();
	    account.setVpa(vpa);
	    account.setBalance(BigDecimal.valueOf(10000));

	    accountRepository.save(account);

	    System.out.println("ACCOUNT SAVED");

	    return user;
	
	}

    private String generateVpa(String phone) {
        return "user"+phone.substring(phone.length()-4) + "@meshpay";
    }
}