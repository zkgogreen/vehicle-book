package com.bengkel.booking.services;

import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static Scanner input = new Scanner(System.in);
	public static void run() {
		boolean isLooping = true;
		do {
//			login();
			mainMenu();
		} while (isLooping);
		
	}

	public static void login() {

		int attempts = 3;
		while (true){
			System.out.print("Masukan Customer ID : ");
			String customerID = input.nextLine();
			System.out.print("Masukan Password : ");
			String password = input.nextLine();

			attempts--;
			if(BengkelService.login(listAllCustomers, customerID, password)){break;}
			if(attempts == 0){System.exit(0);}

		}
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
				BengkelService.Booking(listAllCustomers);
				break;
			case 3:
				BengkelService.TopUp(listAllCustomers);
				break;
			case 4:
				//panggil fitur Informasi Booking Order
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
