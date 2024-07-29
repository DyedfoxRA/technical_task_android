@file:OptIn(ExperimentalMaterial3Api::class)

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.ui.UserViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserGender by remember { mutableStateOf("") }
    var newUserStatus by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            newUserName.isNotBlank() && newUserEmail.isNotBlank() && newUserGender.isNotBlank() && newUserStatus.isNotBlank()
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add User")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val swipeRefreshState = remember { SwipeRefreshState(isLoading) }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.loadPreviousUsers() },
                modifier = Modifier.fillMaxSize()
            ) {
                UserList(
                    users = users,
                    onDeleteUser = { user -> viewModel.deleteUser(user) },
                    listState = listState,
                    isLoadingMore = isLoading
                )
            }

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { index ->
                        if (index == users.size - 1) {
                            viewModel.loadMoreUsers()
                        }
                    }
            }

            if (isLoading && users.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Add User") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            label = { Text("Name") },
                            isError = validationErrors.containsKey("name")
                        )
                        if (validationErrors.containsKey("name")) {
                            Text(
                                text = validationErrors["name"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        OutlinedTextField(
                            value = newUserEmail,
                            onValueChange = { newUserEmail = it },
                            label = { Text("Email") },
                            isError = validationErrors.containsKey("email")
                        )
                        if (validationErrors.containsKey("email")) {
                            Text(
                                text = validationErrors["email"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        OutlinedTextField(
                            value = newUserGender,
                            onValueChange = { newUserGender = it },
                            label = { Text("Gender") },
                            isError = validationErrors.containsKey("gender")
                        )
                        if (validationErrors.containsKey("gender")) {
                            Text(
                                text = validationErrors["gender"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        OutlinedTextField(
                            value = newUserStatus,
                            onValueChange = { newUserStatus = it },
                            label = { Text("Status") },
                            isError = validationErrors.containsKey("status")
                        )
                        if (validationErrors.containsKey("status")) {
                            Text(
                                text = validationErrors["status"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            scope.launch {
                                val success = viewModel.createUser(
                                    newUserName,
                                    newUserEmail,
                                    newUserGender,
                                    newUserStatus
                                )
                                if (success) {
                                    showDialog = false
                                }
                            }
                        },
                        enabled = isFormValid
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun UserList(
    users: List<UserDTO>,
    onDeleteUser: (UserDTO) -> Unit,
    listState: LazyListState,
    isLoadingMore: Boolean
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(users.size) { index ->
            UserItem(user = users[index], onDeleteUser = onDeleteUser)
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun UserItem(user: UserDTO, onDeleteUser: (UserDTO) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Delete User") },
            text = { Text(text = "Are you sure you want to delete this user?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteUser(user)
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Text(text = user.gender, style = MaterialTheme.typography.bodyMedium)
            Text(text = user.status, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
