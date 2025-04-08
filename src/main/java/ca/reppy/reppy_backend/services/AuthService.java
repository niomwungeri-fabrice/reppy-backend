package ca.reppy.reppy_backend.services;
import ca.reppy.reppy_backend.configs.JwtService;
import ca.reppy.reppy_backend.entities.User;
import ca.reppy.reppy_backend.exceptions.InvalidCredentialsException;
import ca.reppy.reppy_backend.exceptions.NotFoundException;
import ca.reppy.reppy_backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String registerUser(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            userRepository.save(user);
        }

        generateOTP(user);
        return "check your email for OTP";
    }

    public String verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (user.getOtp() == null || user.getOtpExpiry() == null || user.isOtpUsed()) {
            throw new InvalidCredentialsException("otp not generated or already used");
        }

        long currentTime = System.currentTimeMillis();
        if (user.getOtp().equals(otp) && user.getOtpExpiry().after(new Date(currentTime))) {
            // OTP is valid, expire it immediately after use
            user.setOtp(null);
            user.setOtpExpiry(null);
            user.setOtpUsed(true);
            userRepository.save(user);
            return jwtService.generateToken(email);
        }
        throw new InvalidCredentialsException("invalid or expired otp");
    }

    private void generateOTP(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        Date expiryTime = new Date(System.currentTimeMillis() + OTP_EXPIRY_TIME);

        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        user.setOtpUsed(false);
        userRepository.save(user);

        mockSendEmail(user.getEmail(), otp);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(System.out.format("user not '%s' found", email).toString()));
    }

    private void mockSendEmail(String email, String otp) {
        System.out.println("mock email sent to " + email + " with OTP: " + otp);
    }


}
