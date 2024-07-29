package com.venture.userlist.ui

import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.repo.UserResponse
import com.venture.userlist.domain.repo.UserRepository
import com.venture.userlist.domain.results.DataError
import com.venture.userlist.domain.results.ResultResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class UserViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val repository: UserRepository = mockk(relaxed = true)
    private lateinit var viewModel: UserViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLastPageUsers success`() = runTest(testDispatcher) {
        val users = listOf(UserDTO(1, "John Doe", "john@example.com", "male", "active"))
        val response = ResultResponse.Success(UserResponse(users, 1, 1))

        coEvery { repository.getUsers(any()) } returns flow { emit(response) }

        viewModel = UserViewModel(repository)

        Assertions.assertTrue(viewModel.users.value is ResultResponse.Success)
        Assertions.assertEquals(users, (viewModel.users.value as ResultResponse.Success).data)
    }

    @Test
    fun `loadLastPageUsers failure`() = runTest(testDispatcher) {
        val response = ResultResponse.Error(DataError.Network.UNKNOWN)

        coEvery { repository.getUsers(any()) } returns flow { emit(response) }

        viewModel = UserViewModel(repository)

        Assertions.assertTrue(viewModel.users.value is ResultResponse.Error)
    }

    @Test
    fun `createUser success`() = runTest(testDispatcher) {
        val newUser = UserDTO(1, "John Doe", "john@example.com", "male", "active")
        val response = ResultResponse.Success(newUser)

        coEvery { repository.createUser(any()) } returns flow { emit(response) }

        val result = viewModel.createUser("John Doe", "john@example.com", "male", "active")

        Assertions.assertTrue(result)
        Assertions.assertTrue(viewModel.users.value is ResultResponse.Success)
        Assertions.assertTrue(
            (viewModel.users.value as ResultResponse.Success).data.contains(newUser)
        )
    }

    @Test
    fun `createUser failure`() = runTest(testDispatcher) {
        val response = ResultResponse.Error(DataError.Network.UNKNOWN)

        coEvery { repository.createUser(any()) } returns flow { emit(response) }

        val result = viewModel.createUser("John Doe", "john@example.com", "male", "active")

        Assertions.assertFalse(result)
        Assertions.assertTrue(viewModel.users.value is ResultResponse.Error)
    }

    @Test
    fun `deleteUser success`() = runTest(testDispatcher) {
        val user = UserDTO(1, "John Doe", "john@example.com", "male", "active")
        val response = ResultResponse.Success(true)

        coEvery { repository.deleteUser(any()) } returns flow { emit(response) }

        viewModel.deleteUser(user)

        Assertions.assertTrue(viewModel.users.value is ResultResponse.Success)
        Assertions.assertFalse((viewModel.users.value as ResultResponse.Success).data.contains(user))
    }

    @Test
    fun `deleteUser failure`() = runTest(testDispatcher) {
        val user = UserDTO(1, "John Doe", "john@example.com", "male", "active")
        val response = ResultResponse.Error(DataError.Network.UNKNOWN)

        coEvery { repository.deleteUser(any()) } returns flow { emit(response) }

        viewModel.deleteUser(user)

        Assertions.assertTrue(viewModel.users.value is ResultResponse.Error)
    }
}
