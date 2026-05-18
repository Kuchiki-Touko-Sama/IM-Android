package io.github.touko.feature.register.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.feature.register.RegisterViewModel
import io.github.touko.navigation.LoginPage
import io.github.touko.navigation.NavigatorManager

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel()
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedCard(
                elevation = CardDefaults.elevatedCardElevation(6.dp),
                modifier = Modifier.padding(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "创建一个账号",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = viewModel::updateUsername,
                        label = { Text("username") },
                        maxLines = 1,
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        shape = RoundedCornerShape(20.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = viewModel::updatePassword,
                        label = { Text("password") },
                        visualTransformation = PasswordVisualTransformation(),
                        maxLines = 1,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        shape = RoundedCornerShape(20.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        label = { Text("confirm password") },
                        visualTransformation = PasswordVisualTransformation(),
                        maxLines = 1,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        shape = RoundedCornerShape(20.dp),
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = { NavigatorManager.goTo(LoginPage) }) {
                            Text("返回登录页面")
                        }

                        Button(
                            onClick = viewModel::register,
                            enabled = !viewModel.isLoading,
                        ) {
                            Text("注册")
                        }
                    }
                }
            }
        }
    }
}
