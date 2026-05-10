package io.github.touko.ui.views.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.touko.navigation.MainPage
import io.github.touko.navigation.RegisterPage


@Composable
fun LoginScreen(
    navigator: MutableList<Any>,
    viewModel: LoginViewModel = viewModel()
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
           Text(
               text = "Login",
               style = MaterialTheme.typography.headlineLarge,
               fontSize = 70.sp,
               fontWeight = FontWeight.Bold,
           )

           Spacer(modifier = Modifier.height(32.dp))

           ElevatedCard(
               colors = CardDefaults.cardColors().copy(
                   containerColor = MaterialTheme.colorScheme.surfaceContainer,
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

                   Spacer(modifier = Modifier.height(50.dp))

                   Row(modifier = Modifier.align(Alignment.End)) {
                       // 跳转到注册页面
                       IconButton(
                           onClick = { navigator.add(RegisterPage) }
                       ) {
                           Icon(
                               imageVector = Icons.Default.PersonAdd,
                               contentDescription = null,
                           )
                       }
                       // 登录
                       IconButton(
                           onClick = {
                               viewModel.login { navigator.add(MainPage) }
                           },
                           enabled = !viewModel.isLoading,
                           colors = IconButtonDefaults.iconButtonColors().copy(
                               containerColor = MaterialTheme.colorScheme.primaryContainer,
                               contentColor = MaterialTheme.colorScheme.primary
                           )
                       ) {
                           Icon(
                               imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                               contentDescription = null
                           )
                       }
                   }
               }
           }
       }
   }
}

