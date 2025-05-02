package ui;

import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import view.LoginView;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginViewTest {

    @Test
    @DisplayName("로그인 성공 시 해당 역할의 대시보드가 열려야 함")
    void successfulLoginShouldOpenDashboard() throws InterruptedException, InvocationTargetException {
        // Given
        User mockUser = new User("TEST001", "password", "테스트", User.UserRole.PROFESSOR);
        UserController mockUserController = mock(UserController.class);
        when(mockUserController.authenticate(anyString(), anyString()))).thenReturn(mockUser);

        SwingUtilities.invokeAndWait(() -> {
            LoginView loginView = new LoginView(mockUserController);
            loginView.setVisible(true);

            // When
            loginView.setTestData("TEST001", "password"); // 테스트용 메서드 가정
            loginView.getLoginButton().doClick(); // 로그인 버튼 클릭
        });

        // Then
        await().untilAsserted(() -> {
            Frame[] frames = Frame.getFrames();
            assertThat(frames)
                    .filteredOn(frame -> frame.isVisible() && frame.getTitle().contains("교수"))
                    .hasSize(1);
        });
    }

    @Test
    @DisplayName("잘못된 로그인 시 에러 메시지가 표시되어야 함")
    void failedLoginShouldShowErrorMessage() throws InterruptedException, InvocationTargetException {
        // Given
        UserController mockUserController = mock(UserController.class);
        when(mockUserController.authenticate(anyString(), anyString()))).thenReturn(null);

        SwingUtilities.invokeAndWait(() -> {
            LoginView loginView = new LoginView(mockUserController);
            loginView.setVisible(true);

            // When
            loginView.setTestData("wrong", "wrong");
            loginView.getLoginButton().doClick();
        });

        // Then
        await().untilAsserted(() -> {
            // JOptionPane 메시지 확인을 위해 Window 목록 검사
            Window[] windows = Window.getWindows();
            assertThat(windows)
                    .filteredOn(window -> window.isShowing() && window instanceof JDialog)
                    .anyMatch(window -> {
                        JDialog dialog = (JDialog) window;
                        return dialog.getTitle().equals("로그인 실패");
                    });
        });
    }
}