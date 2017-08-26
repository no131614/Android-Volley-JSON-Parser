# Android-Volley-JSON-Parser (Store Orders Tracker)
### (Shopify Winter 2018 Internship Mobile Development Problem)

This Android application is created as part of the [Shopify Winter 2018 Internship Mobile Development Problem](https://github.com/no131614/Android-Volley-JSON-Parser/blob/master/readme_task/Mobile%20Development%20Problem%20Winter%202018.txt). This task requires an app that could parse [Customers Orders in JSON](https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6) and displays the necassary information in a simple and informative view. The app contains three main activity pages: Dashboard, Customer Information, and Item Stats Information.  

<p align="center">
  <img src="https://github.com/no131614/Android-Volley-JSON-Parser/blob/master/readme_pictures/Main.png">
</p>

### Note
- Current app API level is 21 (Lollipop 5.0)
- This app was tested on Samsung Galaxy Tab S2 9.7" (Android API level 24 Nougat	7.0) and Samsung Galaxy S4 (Android API level 21 Lollipop	5.0)

## Dashboard
<p align="center">
  <img src="https://github.com/no131614/Android-Volley-JSON-Parser/blob/master/readme_pictures/Dashboard.png">
</p>

The Dashboard activity page displays main overall information:
- Total Items Sold - the total number of items sold in all of the orders
- Total Price Amount - the total price from all of the orders
- Favourite Customer of The Day - the favourite customer base on the top total spent

The Dashboard activity page also acts as the Main Activity of the app and enables the user to access the Customer Info and Item Stats Info activity page using the image buttons.

## Customer Information

<p align="center">
  <img src="https://github.com/no131614/Android-Volley-JSON-Parser/blob/master/readme_pictures/Customer_Info_Large.png">
</p>

The Customer Information activity page displays a list of all available customer names fetch from all of the orders. Upon selecting a customer from the list, the page will then displays the customer information:
- First Name - the customer first name
- Last Name - the customer last name
- ID - the customer unique ID
- Email - the customer email address
- Phone - the customer phone number
- Note - extra information about the customer
- Total price - the customer accumulated total price from all of the customer orders
- Total spent - the customer total spent

Base on the current android device screen orientation: 
- Landscape -> the customer information will be displayed beside the customer names list
- Portrait -> selecting a customer name from the list will bring a new activity page displaying the customer information


Note: If an order does not have a customer object, the app will use the name field on the order for the customer name (a 4 digit number).

## Item Stats Information

<p align="center">
  <img src="https://github.com/no131614/Android-Volley-JSON-Parser/blob/master/readme_pictures/Item_Info.png">
</p>

The Item Stats Information activity page displays a list of all product item names that are in the orders. Upon selecting a item from the list, the page will display a custom dialog that displays the item information:

- Name - the item name
- Product ID - the item unique ID
- Number of Items Sold - the quantity of the item sold
- Total Price - the total price of all of the item in all of the orders
