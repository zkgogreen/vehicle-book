package com.bengkel.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static List<BookingOrder> listAllBookingOrder = new ArrayList<>();
	private static Scanner input = new Scanner(System.in);
	private static Customer customer = new Customer();
	public static void run() {
        do {
			customer = login();
            mainMenu();
        } while (true);
	}

	public static Customer login() {
		Customer customer =  new Customer();
		int attempts = 3;
		while (true){
			System.out.print("Masukan Customer ID : ");
			String customerID = input.nextLine();
			System.out.print("Masukan Password : ");
			String password = input.nextLine();

			attempts--;
			customer = BengkelService.login(listAllCustomers, customerID, password);
			if(customer != null){break;}
			if(attempts == 0){System.exit(0);}

		}
		return customer;
	}
	
	public static void mainMenu() {
		String[] listMenu = {"Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking", "Logout"};
		int menuChoice = 0;
		boolean isLooping = true;


		do {
			PrintService.printMenu(listMenu, "Booking Bengkel Menu");
			menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu : ", "Input Harus Berupa Angka!", "^[0-9]+$", listMenu.length-1, 0);


			switch (menuChoice) {
			case 1:
				BengkelService.infoCustomer(listAllCustomers);
				break;
			case 2:
				listAllBookingOrder.add(BengkelService.Booking(listAllBookingOrder, listAllCustomers, listAllItemService));
				break;
			case 3:
				BengkelService.TopUp(listAllCustomers);
				break;
			case 4:
				//panggil fitur Informasi Booking Order
				BengkelService.listBooking(listAllBookingOrder, customer);
				break;
			default:
				System.out.println("Logout");
				isLooping = false;
				break;
			}
		} while (isLooping);
		
		
	}

	//Silahkan tambahkan kodingan untuk keperluan Menu Aplikasi
}
