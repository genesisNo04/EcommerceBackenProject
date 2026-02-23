package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceDeleteTest extends BaseUserServiceTest{

    @Test
    void deleteUser() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void deleteUser_userNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.deleteUser(1L));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
        verifyNoInteractions(passwordEncoder, addressService);
    }
}
