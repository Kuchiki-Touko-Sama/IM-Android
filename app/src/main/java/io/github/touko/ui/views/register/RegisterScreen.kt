package io.github.touko.ui.views.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.MainPage

@Composable
fun RegisterScreen(
    navigator: MutableList<Any>,
    viewModel: RegisterViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedCard(
            colors = CardDefaults.cardColors().copy(
                containerColor = Color.White,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = viewModel::updateUsername,
                    label = { Text("username") },
                    maxLines = 1,
                    shape = RoundedCornerShape(
                        20.dp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("password") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(
                        20.dp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::updateConfirmPassword,
                    label = { Text("confirm password") },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(
                        20.dp
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))

                viewModel.errorMessage?.let {
                    if (it.isNotEmpty()) {
                        Text(viewModel.errorMessage!!)
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    // 返回登录页面
                    IconButton(
                        onClick = { navigator.add(LoginPage) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                    // 提交注册表单
                    IconButton(
                        onClick = {
                            viewModel.register { navigator.add(MainPage) }
                        },
                        enabled = !viewModel.isLoading,
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }

                }

            }
        }
    }
}
