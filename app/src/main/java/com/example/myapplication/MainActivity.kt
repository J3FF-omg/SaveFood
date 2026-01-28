package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

// Helper функции для создания background
fun AppCompatActivity.createEditTextBackground(): android.graphics.drawable.Drawable {
    val shape = GradientDrawable()
    shape.cornerRadius = 8f
    shape.setStroke(2, 0xFFCCCCCC.toInt())
    shape.setColor(android.graphics.Color.WHITE)
    return shape
}

fun AppCompatActivity.createStatsBackground(): android.graphics.drawable.Drawable {
    val shape = GradientDrawable()
    shape.cornerRadius = 12f
    shape.setStroke(2, 0xFF4CAF50.toInt())
    shape.setColor(0xFFE8F5E9.toInt())
    return shape
}

// ==================== КЛАССЫ ДАННЫХ ====================

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String,
    val role: String, // "admin", "buyer", "seller"
    val email: String,
    val phone: String = "",
    val address: String = ""
) : Serializable

data class Restaurant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val address: String,
    val sellerId: String,
    val phone: String = "",
    val rating: Float = 4.5f,
    val imageUrl: String = ""
) : Serializable

data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val quantity: Int,
    val restaurantId: String,
    val category: String = "Основные блюда",
    val discount: Int = 30,
    val pickupOnly: Boolean = false,
    val imageUrl: String = "",
    val cookingTime: Int = 20 // минут
) : Serializable

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val buyerId: String,
    val foodItems: List<Pair<String, Int>>, // foodId to quantity
    val totalPrice: Double,
    val deliveryAddress: String?,
    val deliveryType: String, // "delivery" или "pickup"
    val status: String = "pending", // pending, preparing, delivered, cancelled
    val restaurantId: String,
    val orderDate: Date = Date(),
    val paymentMethod: String = "card",
    val notes: String = ""
) : Serializable

data class CartItem(
    val foodItem: FoodItem,
    var quantity: Int
) : Serializable

// ==================== СИМУЛЯЦИЯ БАЗЫ ДАННЫХ ====================

object Database {
    val users = mutableListOf<User>()
    val restaurants = mutableListOf<Restaurant>()
    val foodItems = mutableListOf<FoodItem>()
    val orders = mutableListOf<Order>()

    init {
        // Инициализация начальных данных
        users.add(User("1", "admin", "admin123", "admin", "admin@foodrescue.com", "+7 999 1112233"))
        users.add(User("2", "buyer1", "buyer123", "buyer", "buyer@example.com", "+7 999 4445566", "ул. Примерная, 15"))
        users.add(User("3", "seller1", "seller123", "seller", "italian@restaurant.com", "+7 999 7778899"))
        users.add(User("4", "seller2", "seller123", "seller", "sushi@restaurant.com", "+7 999 0001122"))

        restaurants.add(Restaurant(
            "1",
            "Итальянская кухня",
            "Настоящая итальянская кухня с душой",
            "ул. Ленина, 10",
            "3",
            "+7 999 1234567",
            4.7f,
            "https://example.com/italian.jpg"
        ))
        restaurants.add(Restaurant(
            "2",
            "Суши бар Tokyo",
            "Свежие суши и роллы",
            "ул. Пушкина, 25",
            "4",
            "+7 999 7654321",
            4.9f,
            "https://example.com/sushi.jpg"
        ))
        restaurants.add(Restaurant(
            "3",
            "Бургерная",
            "Домашние бургеры и картофель фри",
            "пр. Мира, 50",
            "3",
            "+7 999 8889977",
            4.3f
        ))

        foodItems.add(FoodItem(
            "1",
            "Паста Карбонара",
            "Свежая паста с беконом и соусом из яиц и пармезана",
            450.0,
            650.0,
            5,
            "1",
            "Паста",
            31,
            false,
            "",
            15
        ))
        foodItems.add(FoodItem(
            "2",
            "Пицца Маргарита",
            "Классическая пицца с томатным соусом и моцареллой",
            350.0,
            500.0,
            3,
            "1",
            "Пицца",
            30,
            false,
            "",
            20
        ))
        foodItems.add(FoodItem(
            "3",
            "Ролл Филадельфия",
            "8 шт, свежая рыба, рис, нори, сливочный сыр",
            300.0,
            450.0,
            10,
            "2",
            "Суши и роллы",
            33,
            true,
            "",
            10
        ))
        foodItems.add(FoodItem(
            "4",
            "Суп Том Ям",
            "Острый тайский суп с креветками и кокосовым молоком",
            250.0,
            400.0,
            7,
            "2",
            "Супы",
            38,
            false,
            "",
            15
        ))
        foodItems.add(FoodItem(
            "5",
            "Чизбургер",
            "Бургер с говяжьей котлетой, сыром и овощами",
            280.0,
            400.0,
            8,
            "3",
            "Бургеры",
            30,
            false,
            "",
            12
        ))
        foodItems.add(FoodItem(
            "6",
            "Картофель фри",
            "Хрустящий картофель фри с соусом",
            120.0,
            180.0,
            15,
            "3",
            "Закуски",
            33,
            false,
            "",
            8
        ))

        orders.add(Order(
            "1",
            "2",
            listOf("1" to 2, "5" to 1),
            1180.0,
            "ул. Примерная, 15",
            "delivery",
            "delivered",
            "1",
            Date(System.currentTimeMillis() - 86400000 * 2)
        ))
    }

    fun findUser(username: String, password: String): User? {
        return users.find { it.username == username && it.password == password }
    }

    fun registerUser(username: String, password: String, email: String, phone: String, role: String): Boolean {
        if (users.any { it.username == username }) return false
        users.add(User(
            username = username,
            password = password,
            email = email,
            phone = phone,
            role = role
        ))
        return true
    }

    fun getRestaurantBySeller(sellerId: String): Restaurant? {
        return restaurants.find { it.sellerId == sellerId }
    }

    fun getFoodByRestaurant(restaurantId: String): List<FoodItem> {
        return foodItems.filter { it.restaurantId == restaurantId }
    }

    fun getOrdersByBuyer(buyerId: String): List<Order> {
        return orders.filter { it.buyerId == buyerId }
    }

    fun getOrdersByRestaurant(restaurantId: String): List<Order> {
        return orders.filter { it.restaurantId == restaurantId }
    }

    fun updateFoodQuantity(foodId: String, newQuantity: Int): Boolean {
        val index = foodItems.indexOfFirst { it.id == foodId }
        if (index != -1 && newQuantity >= 0) {
            foodItems[index] = foodItems[index].copy(quantity = newQuantity)
            return true
        }
        return false
    }

    fun updateOrderStatus(orderId: String, newStatus: String): Boolean {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            orders[index] = orders[index].copy(status = newStatus)
            return true
        }
        return false
    }
}

// ==================== ГЛАВНАЯ АКТИВНОСТЬ (АВТОРИЗАЦИЯ) ====================

class MainActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLoginLayout()
    }

    private fun setupLoginLayout() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            gravity = android.view.Gravity.CENTER
        }

        // Заголовок
        val tvTitle = TextView(this).apply {
            text = "🥡 Food Rescue"
            textSize = 32f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            setTextColor(0xFF2E7D32.toInt())
            setPadding(0, 0, 0, 60)
        }

        // Подзаголовок
        val tvSubtitle = TextView(this).apply {
            text = "Спасем еду вместе!"
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }

        // Поля ввода
        etUsername = TextInputEditText(this).apply {
            hint = "Логин"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        etPassword = TextInputEditText(this).apply {
            hint = "Пароль"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        // Кнопки
        btnLogin = Button(this).apply {
            text = "Войти"
            setBackgroundColor(0xFF4CAF50.toInt())
            setTextColor(android.graphics.Color.WHITE)
            setPadding(0, 20, 0, 20)
            setOnClickListener { login() }
        }

        tvRegister = TextView(this).apply {
            text = "Нет аккаунта? Зарегистрироваться"
            gravity = android.view.Gravity.CENTER
            setPadding(0, 30, 0, 0)
            setTextColor(0xFF2196F3.toInt())
            setOnClickListener { showRegistrationDialog() }
        }

        // Добавление элементов
        layout.addView(tvTitle)
        layout.addView(tvSubtitle)
        layout.addView(etUsername, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.addView(etPassword, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.addView(btnLogin, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.addView(tvRegister, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setContentView(layout)
    }

    private fun login() {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val user = Database.findUser(username, password)
        if (user != null) {
            when (user.role) {
                "admin" -> {
                    startActivity(Intent(this, AdminActivity::class.java).apply {
                        putExtra("USER", user)
                    })
                }
                "seller" -> {
                    startActivity(Intent(this, SellerActivity::class.java).apply {
                        putExtra("USER", user)
                    })
                }
                "buyer" -> {
                    startActivity(Intent(this, BuyerActivity::class.java).apply {
                        putExtra("USER", user)
                    })
                }
            }
            finish()
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRegistrationDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etRegUsername = TextInputEditText(this).apply {
            hint = "Логин"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etRegPassword = TextInputEditText(this).apply {
            hint = "Пароль"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val etRegEmail = TextInputEditText(this).apply {
            hint = "Email"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        val etRegPhone = TextInputEditText(this).apply {
            hint = "Телефон"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        val roleSpinner = Spinner(this)
        val roles = arrayOf("Покупатель", "Продавец")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        dialogView.addView(etRegUsername, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etRegPassword, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etRegEmail, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etRegPhone, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(TextView(this).apply {
            text = "Выберите роль:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(roleSpinner, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        AlertDialog.Builder(this)
            .setTitle("Регистрация")
            .setView(dialogView)
            .setPositiveButton("Зарегистрироваться") { _, _ ->
                val username = etRegUsername.text.toString()
                val password = etRegPassword.text.toString()
                val email = etRegEmail.text.toString()
                val phone = etRegPhone.text.toString()
                val role = when (roleSpinner.selectedItem.toString()) {
                    "Продавец" -> "seller"
                    else -> "buyer"
                }

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (Database.registerUser(username, password, email, phone, role)) {
                    Toast.makeText(this, "Регистрация успешна! Теперь войдите", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}

// ==================== АКТИВНОСТЬ АДМИНИСТРАТОРА ====================

class AdminActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var tvStats: TextView
    private lateinit var lvRestaurants: ListView
    private lateinit var btnAddRestaurant: Button
    private lateinit var btnViewUsers: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = intent.getSerializableExtra("USER") as User
        setupAdminLayout()
        loadData()
    }

    private fun setupAdminLayout() {
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // Заголовок
        val tvTitle = TextView(this).apply {
            text = "👑 Панель администратора"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 24)
        }

        // Информация о пользователе
        val tvUserInfo = TextView(this).apply {
            text = "Вы вошли как: ${currentUser.username} (${currentUser.email})"
            setPadding(0, 0, 0, 16)
        }

        // Статистика
        tvStats = TextView(this).apply {
            setPadding(16, 16, 16, 16)
            background = createStatsBackground()
            textSize = 14f
        }

        // Кнопки действий
        val buttonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        btnAddRestaurant = Button(this).apply {
            text = "➕ Добавить ресторан"
            setBackgroundColor(0xFF4CAF50.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 8
            }
            setOnClickListener { showAddRestaurantDialog() }
        }

        btnViewUsers = Button(this).apply {
            text = "👥 Пользователи"
            setBackgroundColor(0xFF2196F3.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { showUsersDialog() }
        }

        buttonsLayout.addView(btnAddRestaurant)
        buttonsLayout.addView(btnViewUsers)

        // Список ресторанов
        val tvRestaurantsTitle = TextView(this).apply {
            text = "Список ресторанов:"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }

        lvRestaurants = ListView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        }

        // Кнопка выхода
        btnLogout = Button(this).apply {
            text = "Выйти"
            setBackgroundColor(0xFFF44336.toInt())
            setTextColor(android.graphics.Color.WHITE)
            setPadding(0, 20, 0, 20)
            setOnClickListener {
                startActivity(Intent(this@AdminActivity, MainActivity::class.java))
                finish()
            }
        }

        // Добавление элементов
        mainLayout.addView(tvTitle)
        mainLayout.addView(tvUserInfo)
        mainLayout.addView(tvStats)
        mainLayout.addView(buttonsLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        mainLayout.addView(tvRestaurantsTitle)
        mainLayout.addView(lvRestaurants)
        mainLayout.addView(btnLogout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun loadData() {
        updateStats()
        updateRestaurantsList()
    }

    private fun updateStats() {
        val stats = """
            📊 Статистика системы:
            
            👥 Пользователей: ${Database.users.size}
            🏪 Ресторанов: ${Database.restaurants.size}
            🍽️ Блюд в продаже: ${Database.foodItems.size}
            📦 Активных заказов: ${Database.orders.size}
            
            Продавцов: ${Database.users.count { it.role == "seller" }}
            Покупателей: ${Database.users.count { it.role == "buyer" }}
            
            Всего спасено еды: ${Database.foodItems.sumOf { it.quantity }} порций
        """.trimIndent()

        tvStats.text = stats
    }

    private fun updateRestaurantsList() {
        val restaurantStrings = Database.restaurants.map { restaurant ->
            val seller = Database.users.find { it.id == restaurant.sellerId }
            """
            🏪 ${restaurant.name}
            📍 ${restaurant.address}
            📞 ${restaurant.phone}
            ⭐ Рейтинг: ${restaurant.rating}
            👨‍🍳 Продавец: ${seller?.username ?: "Не найден"}
            ---
            """.trimIndent()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurantStrings)
        lvRestaurants.adapter = adapter

        lvRestaurants.setOnItemClickListener { _, _, position, _ ->
            val restaurant = Database.restaurants[position]
            showRestaurantDetailsDialog(restaurant)
        }
    }

    private fun showAddRestaurantDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etName = TextInputEditText(this).apply {
            hint = "Название ресторана"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etDescription = TextInputEditText(this).apply {
            hint = "Описание"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etAddress = TextInputEditText(this).apply {
            hint = "Адрес"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etPhone = TextInputEditText(this).apply {
            hint = "Телефон"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        val sellers = Database.users.filter { it.role == "seller" }
        val sellerSpinner = Spinner(this)
        val sellerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sellers.map { "${it.username} (${it.email})" })
        sellerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sellerSpinner.adapter = sellerAdapter

        dialogView.addView(TextView(this).apply {
            text = "Выберите продавца:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(sellerSpinner)
        dialogView.addView(etName, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etDescription, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etAddress, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etPhone, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        AlertDialog.Builder(this)
            .setTitle("Добавить ресторан")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = etName.text.toString()
                val description = etDescription.text.toString()
                val address = etAddress.text.toString()
                val phone = etPhone.text.toString()

                if (name.isEmpty() || address.isEmpty()) {
                    Toast.makeText(this, "Заполните название и адрес", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (sellers.isEmpty()) {
                    Toast.makeText(this, "Нет зарегистрированных продавцов", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedSeller = sellers[sellerSpinner.selectedItemPosition]
                Database.restaurants.add(Restaurant(
                    name = name,
                    description = description,
                    address = address,
                    sellerId = selectedSeller.id,
                    phone = phone
                ))

                updateRestaurantsList()
                updateStats()
                Toast.makeText(this, "Ресторан добавлен", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRestaurantDetailsDialog(restaurant: Restaurant) {
        val seller = Database.users.find { it.id == restaurant.sellerId }
        val restaurantFood = Database.foodItems.filter { it.restaurantId == restaurant.id }

        val details = """
            🏪 ${restaurant.name}
            
            📝 ${restaurant.description}
            📍 Адрес: ${restaurant.address}
            📞 Телефон: ${restaurant.phone}
            ⭐ Рейтинг: ${restaurant.rating}
            
            👨‍🍳 Продавец:
            - Логин: ${seller?.username}
            - Email: ${seller?.email}
            - Телефон: ${seller?.phone}
            
            🍽️ Блюд в продаже: ${restaurantFood.size}
            📦 Заказов: ${Database.orders.count { it.restaurantId == restaurant.id }}
            
            🏷️ Доступные блюда:
            ${restaurantFood.joinToString("\n") { "• ${it.name} - ${it.price}₽ (${it.quantity} шт)" }}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Информация о ресторане")
            .setMessage(details)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("Удалить") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Удалить ресторан?")
                    .setMessage("Вы уверены, что хотите удалить ресторан ${restaurant.name}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        Database.restaurants.remove(restaurant)
                        updateRestaurantsList()
                        updateStats()
                        Toast.makeText(this, "Ресторан удален", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            .show()
    }

    private fun showUsersDialog() {
        val usersText = Database.users.joinToString("\n\n") { user ->
            """
            👤 ${user.username} (${user.role})
            📧 ${user.email}
            📞 ${user.phone}
            🆔 ID: ${user.id.take(8)}...
            """.trimIndent()
        }

        AlertDialog.Builder(this)
            .setTitle("Все пользователи (${Database.users.size})")
            .setMessage(usersText)
            .setPositiveButton("Закрыть", null)
            .show()
    }
}

// ==================== АКТИВНОСТЬ ПРОДАВЦА ====================

class SellerActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var tvRestaurantInfo: TextView
    private lateinit var lvFoodItems: ListView
    private lateinit var btnAddFood: Button
    private lateinit var btnViewOrders: Button
    private lateinit var btnLogout: Button
    private var restaurant: Restaurant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = intent.getSerializableExtra("USER") as User
        restaurant = Database.getRestaurantBySeller(currentUser.id)
        setupSellerLayout()
        loadData()
    }

    private fun setupSellerLayout() {
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // Заголовок
        val tvTitle = TextView(this).apply {
            text = "👨‍🍳 Панель продавца"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 24)
        }

        // Информация о пользователе
        val tvUserInfo = TextView(this).apply {
            text = "Вы вошли как: ${currentUser.username}"
            setPadding(0, 0, 0, 16)
        }

        // Информация о ресторане
        tvRestaurantInfo = TextView(this).apply {
            setPadding(16, 16, 16, 16)
            background = createStatsBackground()
            textSize = 14f
        }

        // Кнопки действий
        val buttonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        btnAddFood = Button(this).apply {
            text = "➕ Добавить блюдо"
            setBackgroundColor(0xFF4CAF50.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 8
            }
            setOnClickListener { showAddFoodDialog() }
        }

        btnViewOrders = Button(this).apply {
            text = "📦 Заказы"
            setBackgroundColor(0xFF2196F3.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { showOrdersDialog() }
        }

        buttonsLayout.addView(btnAddFood)
        buttonsLayout.addView(btnViewOrders)

        // Список блюд
        val tvFoodTitle = TextView(this).apply {
            text = "Мои блюда:"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }

        lvFoodItems = ListView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        }

        // Кнопка выхода
        btnLogout = Button(this).apply {
            text = "Выйти"
            setBackgroundColor(0xFFF44336.toInt())
            setTextColor(android.graphics.Color.WHITE)
            setPadding(0, 20, 0, 20)
            setOnClickListener {
                startActivity(Intent(this@SellerActivity, MainActivity::class.java))
                finish()
            }
        }

        mainLayout.addView(tvTitle)
        mainLayout.addView(tvUserInfo)
        mainLayout.addView(tvRestaurantInfo)
        mainLayout.addView(buttonsLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        mainLayout.addView(tvFoodTitle)
        mainLayout.addView(lvFoodItems)
        mainLayout.addView(btnLogout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun loadData() {
        updateRestaurantInfo()
        updateFoodList()
    }

    private fun updateRestaurantInfo() {
        restaurant = Database.getRestaurantBySeller(currentUser.id)

        val info = if (restaurant != null) {
            """
            🏪 Ваш ресторан: ${restaurant!!.name}
            📍 Адрес: ${restaurant!!.address}
            📞 Телефон: ${restaurant!!.phone}
            ⭐ Рейтинг: ${restaurant!!.rating}
            
            🍽️ Блюд в продаже: ${Database.getFoodByRestaurant(restaurant!!.id).size}
            📦 Активных заказов: ${Database.getOrdersByRestaurant(restaurant!!.id).count { it.status != "delivered" && it.status != "cancelled" }}
            """.trimIndent()
        } else {
            "⚠️ У вас еще нет ресторана. Обратитесь к администратору."
        }

        tvRestaurantInfo.text = info

        if (restaurant == null) {
            btnAddFood.isEnabled = false
            btnViewOrders.isEnabled = false
        }
    }

    private fun updateFoodList() {
        val foodList = if (restaurant != null) {
            Database.getFoodByRestaurant(restaurant!!.id)
        } else {
            emptyList()
        }

        val foodStrings = foodList.map { food ->
            """
            🍽️ ${food.name}
            💰 ${food.price}₽ (было ${food.originalPrice}₽, скидка ${food.discount}%)
            📦 Осталось: ${food.quantity} шт.
            🕐 Готовится: ${food.cookingTime} мин
            ${if (food.pickupOnly) "🚫 Только самовывоз" else "✓ Доставка доступна"}
            ---
            """.trimIndent()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodStrings)
        lvFoodItems.adapter = adapter

        lvFoodItems.setOnItemClickListener { _, _, position, _ ->
            if (restaurant != null) {
                val food = Database.getFoodByRestaurant(restaurant!!.id)[position]
                showFoodDetailsDialog(food)
            }
        }
    }

    private fun showAddFoodDialog() {
        if (restaurant == null) {
            Toast.makeText(this, "Сначала создайте ресторан", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etName = TextInputEditText(this).apply {
            hint = "Название блюда"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etDescription = TextInputEditText(this).apply {
            hint = "Описание"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etOriginalPrice = TextInputEditText(this).apply {
            hint = "Оригинальная цена"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val etQuantity = TextInputEditText(this).apply {
            hint = "Количество порций"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val etCookingTime = TextInputEditText(this).apply {
            hint = "Время приготовления (мин)"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val categories = arrayOf("Основные блюда", "Супы", "Салаты", "Десерты", "Напитки", "Закуски", "Суши и роллы", "Пицца", "Паста", "Бургеры")
        val categorySpinner = Spinner(this)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val cbPickupOnly = CheckBox(this).apply {
            text = "Только самовывоз"
        }

        val etDiscount = TextInputEditText(this).apply {
            hint = "Скидка % (авто: 30%)"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText("30")
        }

        dialogView.addView(TextView(this).apply {
            text = "Категория:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(categorySpinner)
        dialogView.addView(etName, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etDescription, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etOriginalPrice, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etQuantity, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etCookingTime, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(etDiscount, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.addView(cbPickupOnly, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        AlertDialog.Builder(this)
            .setTitle("Добавить блюдо")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = etName.text.toString()
                val description = etDescription.text.toString()
                val originalPrice = etOriginalPrice.text.toString().toDoubleOrNull()
                val quantity = etQuantity.text.toString().toIntOrNull()
                val cookingTime = etCookingTime.text.toString().toIntOrNull() ?: 20
                val discount = etDiscount.text.toString().toIntOrNull() ?: 30

                if (name.isEmpty() || description.isEmpty() || originalPrice == null || quantity == null) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = originalPrice * (100 - discount) / 100

                Database.foodItems.add(FoodItem(
                    name = name,
                    description = description,
                    price = price,
                    originalPrice = originalPrice,
                    quantity = quantity,
                    restaurantId = restaurant!!.id,
                    category = categorySpinner.selectedItem.toString(),
                    discount = discount,
                    pickupOnly = cbPickupOnly.isChecked,
                    cookingTime = cookingTime
                ))

                updateFoodList()
                Toast.makeText(this, "Блюдо добавлено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showFoodDetailsDialog(food: FoodItem) {
        val restaurant = Database.restaurants.find { it.id == food.restaurantId }

        val details = """
            🍽️ ${food.name}
            
            📝 ${food.description}
            
            💰 Цена: ${food.price}₽
            💸 Было: ${food.originalPrice}₽
            🏷️ Скидка: ${food.discount}%
            
            📦 В наличии: ${food.quantity} порций
            🕐 Время приготовления: ${food.cookingTime} мин
            📂 Категория: ${food.category}
            
            🏪 Ресторан: ${restaurant?.name ?: "Не найден"}
            📍 ${restaurant?.address ?: ""}
            
            ${if (food.pickupOnly) "🚫 Только самовывоз" else "✓ Доставка доступна"}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Информация о блюде")
            .setMessage(details)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("Изменить количество") { _, _ ->
                showUpdateQuantityDialog(food)
            }
            .setNegativeButton("Удалить") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Удалить блюдо?")
                    .setMessage("Вы уверены, что хотите удалить ${food.name}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        Database.foodItems.remove(food)
                        updateFoodList()
                        Toast.makeText(this, "Блюдо удалено", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            .show()
    }

    private fun showUpdateQuantityDialog(food: FoodItem) {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etNewQuantity = TextInputEditText(this).apply {
            hint = "Новое количество"
            setText(food.quantity.toString())
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        dialogView.addView(TextView(this).apply {
            text = "Текущее количество: ${food.quantity}"
            setPadding(0, 0, 0, 16)
        })
        dialogView.addView(etNewQuantity)

        AlertDialog.Builder(this)
            .setTitle("Изменить количество")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newQuantity = etNewQuantity.text.toString().toIntOrNull()
                if (newQuantity != null && newQuantity >= 0) {
                    Database.updateFoodQuantity(food.id, newQuantity)
                    updateFoodList()
                    Toast.makeText(this, "Количество обновлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Введите корректное число", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showOrdersDialog() {
        if (restaurant == null) return

        val orders = Database.getOrdersByRestaurant(restaurant!!.id)

        if (orders.isEmpty()) {
            Toast.makeText(this, "Нет заказов", Toast.LENGTH_SHORT).show()
            return
        }

        val ordersText = orders.joinToString("\n\n") { order ->
            val buyer = Database.users.find { it.id == order.buyerId }
            val statusEmoji = when (order.status) {
                "pending" -> "⏳"
                "preparing" -> "👨‍🍳"
                "delivered" -> "✅"
                "cancelled" -> "❌"
                else -> "📦"
            }

            """
            ${statusEmoji} Заказ #${order.id.take(8)}
            📅 ${order.orderDate}
            👤 Покупатель: ${buyer?.username ?: "Неизвестно"}
            💰 Сумма: ${order.totalPrice}₽
            📍 Тип: ${if (order.deliveryType == "delivery") "Доставка" else "Самовывоз"}
            🏷️ Статус: ${when(order.status) {
                "pending" -> "Ожидает"
                "preparing" -> "Готовится"
                "delivered" -> "Доставлен"
                "cancelled" -> "Отменен"
                else -> order.status
            }}
            """.trimIndent()
        }

        AlertDialog.Builder(this)
            .setTitle("Заказы (${orders.size})")
            .setMessage(ordersText)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("Изменить статус") { _, _ ->
                showUpdateOrderStatusDialog()
            }
            .show()
    }

    private fun showUpdateOrderStatusDialog() {
        if (restaurant == null) return

        val orders = Database.getOrdersByRestaurant(restaurant!!.id)
        if (orders.isEmpty()) return

        val orderTitles = orders.map { order ->
            val buyer = Database.users.find { it.id == order.buyerId }
            "Заказ #${order.id.take(8)} от ${buyer?.username ?: "Неизвестно"} - ${order.totalPrice}₽"
        }

        AlertDialog.Builder(this)
            .setTitle("Выберите заказ")
            .setItems(orderTitles.toTypedArray()) { _, which ->
                val selectedOrder = orders[which]
                showStatusSelectionDialog(selectedOrder)
            }
            .show()
    }

    private fun showStatusSelectionDialog(order: Order) {
        val statuses = arrayOf("⏳ Ожидает", "👨‍🍳 Готовится", "✅ Доставлен", "❌ Отменен")

        AlertDialog.Builder(this)
            .setTitle("Изменить статус заказа #${order.id.take(8)}")
            .setItems(statuses) { _, which ->
                val newStatus = when (which) {
                    0 -> "pending"
                    1 -> "preparing"
                    2 -> "delivered"
                    3 -> "cancelled"
                    else -> "pending"
                }

                Database.updateOrderStatus(order.id, newStatus)
                Toast.makeText(this, "Статус обновлен", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}

// ==================== АКТИВНОСТЬ ПОКУПАТЕЛЯ ====================

class BuyerActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var lvFoodItems: ListView
    private lateinit var btnCart: Button
    private lateinit var btnOrders: Button
    private lateinit var btnProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var restaurantSpinner: Spinner

    private val cart = mutableListOf<CartItem>()
    private val categories = arrayOf("Все категории", "Основные блюда", "Супы", "Салаты", "Десерты", "Напитки", "Закуски", "Суши и роллы", "Пицца", "Паста", "Бургеры")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = intent.getSerializableExtra("USER") as User
        setupBuyerLayout()
        loadData()
    }

    private fun setupBuyerLayout() {
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Заголовок
        val tvTitle = TextView(this).apply {
            text = "🛒 Food Rescue - Покупатель"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        }

        // Приветствие
        val tvGreeting = TextView(this).apply {
            text = "👋 Привет, ${currentUser.username}!"
            textSize = 16f
            setPadding(0, 0, 0, 16)
        }

        // Фильтры
        val tvFilterTitle = TextView(this).apply {
            text = "Фильтры:"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        // Спиннер категорий
        val categoryLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        categoryLayout.addView(TextView(this).apply {
            text = "Категория:"
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f)
        })

        categorySpinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f)
        }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFoodList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        categoryLayout.addView(categorySpinner)

        // Спиннер ресторанов
        val restaurantLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        restaurantLayout.addView(TextView(this).apply {
            text = "Ресторан:"
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f)
        })

        restaurantSpinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f)
        }
        val restaurantNames = mutableListOf("Все рестораны")
        restaurantNames.addAll(Database.restaurants.map { it.name })
        val restaurantAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, restaurantNames)
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        restaurantSpinner.adapter = restaurantAdapter
        restaurantSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateFoodList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        restaurantLayout.addView(restaurantSpinner)

        // Кнопки действий
        val buttonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        btnCart = Button(this).apply {
            text = "🛒 Корзина (0)"
            setBackgroundColor(0xFFFF9800.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 8
            }
            setOnClickListener { showCart() }
        }

        btnOrders = Button(this).apply {
            text = "📦 Мои заказы"
            setBackgroundColor(0xFF2196F3.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 8
            }
            setOnClickListener { showMyOrders() }
        }

        btnProfile = Button(this).apply {
            text = "👤 Профиль"
            setBackgroundColor(0xFF9C27B0.toInt())
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener { showProfileDialog() }
        }

        buttonsLayout.addView(btnCart)
        buttonsLayout.addView(btnOrders)
        buttonsLayout.addView(btnProfile)

        // Список блюд
        val tvFoodTitle = TextView(this).apply {
            text = "🍽️ Доступные блюда:"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 20, 0, 16)
        }

        lvFoodItems = ListView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600)
        }

        // Кнопка выхода
        btnLogout = Button(this).apply {
            text = "Выйти"
            setBackgroundColor(0xFFF44336.toInt())
            setTextColor(android.graphics.Color.WHITE)
            setPadding(0, 20, 0, 20)
            setOnClickListener {
                startActivity(Intent(this@BuyerActivity, MainActivity::class.java))
                finish()
            }
        }

        // Добавление элементов
        mainLayout.addView(tvTitle)
        mainLayout.addView(tvGreeting)
        mainLayout.addView(tvFilterTitle)
        mainLayout.addView(categoryLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        mainLayout.addView(restaurantLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        mainLayout.addView(buttonsLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        mainLayout.addView(tvFoodTitle)
        mainLayout.addView(lvFoodItems)
        mainLayout.addView(btnLogout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun loadData() {
        updateFoodList()
    }

    private fun updateFoodList() {
        val selectedCategory = categorySpinner.selectedItem.toString()
        val selectedRestaurant = restaurantSpinner.selectedItem.toString()

        val filteredFood = Database.foodItems.filter { food ->
            val restaurant = Database.restaurants.find { it.id == food.restaurantId }
            val categoryMatch = selectedCategory == "Все категории" || food.category == selectedCategory
            val restaurantMatch = selectedRestaurant == "Все рестораны" || restaurant?.name == selectedRestaurant
            categoryMatch && restaurantMatch && food.quantity > 0
        }

        val foodStrings = filteredFood.map { food ->
            val restaurant = Database.restaurants.find { it.id == food.restaurantId }
            val discountEmoji = if (food.discount >= 40) "🔥" else "🏷️"

            """
            ${if (food.pickupOnly) "🚫" else "✓"} ${food.name}
            ${discountEmoji} ${food.price}₽ (было ${food.originalPrice}₽) -${food.discount}%
            🏪 ${restaurant?.name ?: "Неизвестно"}
            📍 ${restaurant?.address ?: ""}
            🕐 ${food.cookingTime} мин | 📦 ${food.quantity} шт.
            ${if (food.quantity < 5) "⚠️ Заканчивается!" else ""}
            ---
            """.trimIndent()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodStrings)
        lvFoodItems.adapter = adapter

        lvFoodItems.setOnItemClickListener { _, _, position, _ ->
            val food = filteredFood[position]
            showFoodDetailsDialog(food)
        }

        // Обновляем счетчик корзины
        updateCartCounter()
    }

    private fun updateCartCounter() {
        val totalItems = cart.sumOf { it.quantity }
        btnCart.text = "🛒 Корзина ($totalItems)"
    }

    private fun showFoodDetailsDialog(food: FoodItem) {
        val restaurant = Database.restaurants.find { it.id == food.restaurantId }
        val cartItem = cart.find { it.foodItem.id == food.id }

        val details = """
            🍽️ ${food.name}
            
            📝 ${food.description}
            
            💰 Цена: ${food.price}₽
            💸 Было: ${food.originalPrice}₽
            🏷️ Скидка: ${food.discount}%
            
            🏪 Ресторан: ${restaurant?.name ?: "Неизвестно"}
            📍 Адрес: ${restaurant?.address ?: ""}
            📞 Телефон: ${restaurant?.phone ?: ""}
            
            🕐 Время приготовления: ${food.cookingTime} мин
            📦 В наличии: ${food.quantity} порций
            ${if (food.pickupOnly) "🚫 Только самовывоз" else "✓ Доставка доступна"}
            
            ${if (cartItem != null) "В корзине: ${cartItem.quantity} шт." else ""}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle(food.name)
            .setMessage(details)
            .setPositiveButton("Добавить в корзину") { _, _ ->
                if (food.quantity <= 0) {
                    Toast.makeText(this, "Блюдо закончилось", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val existingItem = cart.find { it.foodItem.id == food.id }
                if (existingItem != null) {
                    if (existingItem.quantity < food.quantity) {
                        existingItem.quantity++
                        Toast.makeText(this, "Добавлено ещё одна порция", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Недостаточно товара на складе", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    cart.add(CartItem(food, 1))
                    Toast.makeText(this, "Добавлено в корзину", Toast.LENGTH_SHORT).show()
                }

                updateCartCounter()
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun showCart() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val cartText = cart.joinToString("\n\n") { item ->
            """
            🍽️ ${item.foodItem.name}
            💰 ${item.foodItem.price}₽ x ${item.quantity} = ${item.foodItem.price * item.quantity}₽
            """.trimIndent()
        }

        val total = cart.sumOf { it.foodItem.price * it.quantity }
        val totalItems = cart.sumOf { it.quantity }

        val fullText = """
            $cartText
            
            ====================
            📦 Всего товаров: $totalItems
            💰 Общая сумма: ${total}₽
            🏷️ Скидка составила: ${cart.sumOf { (it.foodItem.originalPrice - it.foodItem.price) * it.quantity }}₽
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("🛒 Ваша корзина")
            .setMessage(fullText)
            .setPositiveButton("Оформить заказ") { _, _ ->
                checkout()
            }
            .setNegativeButton("Очистить корзину") { _, _ ->
                cart.clear()
                updateCartCounter()
                Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Редактировать") { _, _ ->
                showEditCartDialog()
            }
            .show()
    }

    private fun showEditCartDialog() {
        if (cart.isEmpty()) return

        val items = cart.mapIndexed { index, item ->
            "${item.foodItem.name} - ${item.quantity} шт."
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Редактировать корзину")
            .setItems(items) { _, which ->
                val selectedItem = cart[which]
                showEditCartItemDialog(selectedItem, which)
            }
            .show()
    }

    private fun showEditCartItemDialog(cartItem: CartItem, position: Int) {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etQuantity = TextInputEditText(this).apply {
            hint = "Количество"
            setText(cartItem.quantity.toString())
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        dialogView.addView(TextView(this).apply {
            text = "${cartItem.foodItem.name}\nМакс: ${cartItem.foodItem.quantity} шт."
            setPadding(0, 0, 0, 16)
        })
        dialogView.addView(etQuantity)

        AlertDialog.Builder(this)
            .setTitle("Изменить количество")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newQuantity = etQuantity.text.toString().toIntOrNull()
                if (newQuantity != null) {
                    if (newQuantity <= 0) {
                        cart.removeAt(position)
                        Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show()
                    } else if (newQuantity <= cartItem.foodItem.quantity) {
                        cartItem.quantity = newQuantity
                        Toast.makeText(this, "Количество обновлено", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Недостаточно товара на складе", Toast.LENGTH_SHORT).show()
                    }
                }
                updateCartCounter()
            }
            .setNegativeButton("Удалить") { _, _ ->
                cart.removeAt(position)
                updateCartCounter()
                Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun checkout() {
        if (cart.isEmpty()) return

        // Проверяем наличие товаров
        for (cartItem in cart) {
            val food = Database.foodItems.find { it.id == cartItem.foodItem.id }
            if (food == null || food.quantity < cartItem.quantity) {
                Toast.makeText(this, "${cartItem.foodItem.name} закончился или недоступен", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Определяем ресторан (берем первый из корзины)
        val firstFood = cart.first().foodItem
        val restaurantId = firstFood.restaurantId

        // Диалог оформления заказа
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val rgDeliveryType = RadioGroup(this)
        val rbDelivery = RadioButton(this).apply {
            text = "🚚 Доставка курьером"
            id = View.generateViewId()
        }
        val rbPickup = RadioButton(this).apply {
            text = "🏃 Самовывоз"
            id = View.generateViewId()
        }
        rgDeliveryType.addView(rbDelivery)
        rgDeliveryType.addView(rbPickup)
        rgDeliveryType.check(rbDelivery.id)

        val etAddress = TextInputEditText(this).apply {
            hint = "Адрес доставки"
            setText(currentUser.address)
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val etNotes = TextInputEditText(this).apply {
            hint = "Комментарий к заказу"
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        val rgPayment = RadioGroup(this)
        val rbCard = RadioButton(this).apply {
            text = "💳 Карта онлайн"
            id = View.generateViewId()
        }
        val rbCash = RadioButton(this).apply {
            text = "💵 Наличные при получении"
            id = View.generateViewId()
        }
        rgPayment.addView(rbCard)
        rgPayment.addView(rbCash)
        rgPayment.check(rbCard.id)

        dialogView.addView(TextView(this).apply {
            text = "Способ получения:"
            setPadding(0, 0, 0, 8)
        })
        dialogView.addView(rgDeliveryType)
        dialogView.addView(TextView(this).apply {
            text = "Адрес доставки:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(etAddress)
        dialogView.addView(TextView(this).apply {
            text = "Способ оплаты:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(rgPayment)
        dialogView.addView(TextView(this).apply {
            text = "Комментарий:"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(etNotes)

        val total = cart.sumOf { it.foodItem.price * it.quantity }

        AlertDialog.Builder(this)
            .setTitle("Оформление заказа")
            .setMessage("Итоговая сумма: ${total}₽")
            .setView(dialogView)
            .setPositiveButton("Подтвердить заказ") { _, _ ->
                val deliveryType = if (rgDeliveryType.checkedRadioButtonId == rbDelivery.id) "delivery" else "pickup"
                val address = etAddress.text.toString()
                val notes = etNotes.text.toString()
                val paymentMethod = if (rgPayment.checkedRadioButtonId == rbCard.id) "card" else "cash"

                if (deliveryType == "delivery" && address.isEmpty()) {
                    Toast.makeText(this, "Введите адрес доставки", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Создаем заказ
                val order = Order(
                    buyerId = currentUser.id,
                    foodItems = cart.map { it.foodItem.id to it.quantity },
                    totalPrice = total,
                    deliveryAddress = if (address.isNotEmpty()) address else null,
                    deliveryType = deliveryType,
                    restaurantId = restaurantId,
                    paymentMethod = paymentMethod,
                    notes = notes
                )
                Database.orders.add(order)

                // Обновляем количество товаров
                for (cartItem in cart) {
                    val food = Database.foodItems.find { it.id == cartItem.foodItem.id }
                    if (food != null) {
                        Database.updateFoodQuantity(food.id, food.quantity - cartItem.quantity)
                    }
                }

                // Очищаем корзину
                cart.clear()
                updateCartCounter()
                updateFoodList()

                // Показываем подтверждение
                val orderDetails = """
                    ✅ Заказ успешно оформлен!
                    
                    Номер заказа: #${order.id.take(8)}
                    Сумма: ${total}₽
                    Способ получения: ${if (deliveryType == "delivery") "Доставка" else "Самовывоз"}
                    ${if (deliveryType == "delivery") "Адрес: $address" else ""}
                    Статус: Ожидает подтверждения
                    
                    Спасибо, что помогаете спасать еду! 🌱
                """.trimIndent()

                AlertDialog.Builder(this)
                    .setTitle("Заказ принят!")
                    .setMessage(orderDetails)
                    .setPositiveButton("OK") { _, _ ->
                        // Обновляем список блюд
                        updateFoodList()
                    }
                    .show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showMyOrders() {
        val orders = Database.getOrdersByBuyer(currentUser.id)

        if (orders.isEmpty()) {
            Toast.makeText(this, "У вас пока нет заказов", Toast.LENGTH_SHORT).show()
            return
        }

        val ordersText = orders.sortedByDescending { it.orderDate }.joinToString("\n\n") { order ->
            val restaurant = Database.restaurants.find { it.id == order.restaurantId }
            val statusEmoji = when (order.status) {
                "pending" -> "⏳"
                "preparing" -> "👨‍🍳"
                "delivered" -> "✅"
                "cancelled" -> "❌"
                else -> "📦"
            }

            """
            ${statusEmoji} Заказ #${order.id.take(8)}
            📅 ${order.orderDate}
            🏪 Ресторан: ${restaurant?.name ?: "Неизвестно"}
            💰 Сумма: ${order.totalPrice}₽
            📍 Тип: ${if (order.deliveryType == "delivery") "Доставка" else "Самовывоз"}
            🏷️ Статус: ${when(order.status) {
                "pending" -> "Ожидает"
                "preparing" -> "Готовится"
                "delivered" -> "Доставлен"
                "cancelled" -> "Отменен"
                else -> order.status
            }}
            """.trimIndent()
        }

        AlertDialog.Builder(this)
            .setTitle("📦 Мои заказы (${orders.size})")
            .setMessage(ordersText)
            .setPositiveButton("Закрыть", null)
            .show()
    }

    private fun showProfileDialog() {
        val orders = Database.getOrdersByBuyer(currentUser.id)
        val deliveredOrders = orders.count { it.status == "delivered" }
        val totalSpent = orders.filter { it.status != "cancelled" }.sumOf { it.totalPrice }
        val foodSaved = orders.sumOf { order ->
            order.foodItems.sumOf { (foodId, quantity) ->
                val food = Database.foodItems.find { it.id == foodId }
                quantity
            }
        }

        val profileInfo = """
            👤 Профиль покупателя
            
            Имя: ${currentUser.username}
            Email: ${currentUser.email}
            Телефон: ${currentUser.phone}
            Адрес: ${currentUser.address}
            
            📊 Статистика:
            
            🛒 Всего заказов: ${orders.size}
            ✅ Выполнено: ${deliveredOrders}
            💰 Потрачено: ${totalSpent}₽
            🍽️ Спасено порций: ${foodSaved}
            
            🌱 Спасибо за ваш вклад
            в борьбу с пищевыми отходами!
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Мой профиль")
            .setMessage(profileInfo)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("Редактировать профиль") { _, _ ->
                showEditProfileDialog()
            }
            .show()
    }

    private fun showEditProfileDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val etPhone = TextInputEditText(this).apply {
            hint = "Телефон"
            setText(currentUser.phone)
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }

        val etAddress = TextInputEditText(this).apply {
            hint = "Адрес"
            setText(currentUser.address)
            setPadding(20, 20, 20, 20)
            background = createEditTextBackground()
        }

        dialogView.addView(TextView(this).apply {
            text = "Телефон:"
            setPadding(0, 0, 0, 8)
        })
        dialogView.addView(etPhone)
        dialogView.addView(TextView(this).apply {
            text = "Адрес (для доставки):"
            setPadding(0, 16, 0, 8)
        })
        dialogView.addView(etAddress)

        AlertDialog.Builder(this)
            .setTitle("Редактировать профиль")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val phone = etPhone.text.toString()
                val address = etAddress.text.toString()

                // В реальном приложении здесь было бы обновление в БД
                Toast.makeText(this, "В реальном приложении данные сохранятся в БД", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}