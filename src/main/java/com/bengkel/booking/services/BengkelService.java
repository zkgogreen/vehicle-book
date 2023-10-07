package com.bengkel.booking.services;

import com.bengkel.booking.models.*;
import com.bengkel.booking.repositories.ItemServiceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BengkelService {
	
	//Silahkan tambahkan fitur-fitur utama aplikasi disini
    private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();


	//Login
	public static boolean login(List<Customer> listAllCustomers,String customerID, String password){
        boolean isSuccess = false;
        for(Customer customer : listAllCustomers){
            if(customer.getCustomerId().equals(customerID)){
                if(customer.getPassword().equals(password)){
                    isSuccess = true;
                }else{
                    System.out.println("Password yang anda Masukan Salah!");
                }
                break;
            }
            System.out.println("Customer Id Tidak Ditemukan atau Salah!");
            break;
        }
        return isSuccess;
    }

	//Info Customer
    public static void infoCustomer(List<Customer> listAllCustomers){

        Scanner input = new Scanner(System.in);
        String formatTable = "| %-2s | %-15s | %-10s | %-10s | %-15s | %-15s |%n";
        String line = "+----+-----------------+------------+------------+-----------------+-----------------+%n";
        System.out.printf(line);
        System.out.format(formatTable, "No", "name", "Adress","Status", "Mobil", "Motor");
        System.out.printf(line);
        int number = 1;
        String mobil = "-",motor="-";
        for (Customer customer : listAllCustomers) {
            String member = (customer instanceof MemberCustomer)?"Member":"Non Member";
            for(Vehicle vehicle : customer.getVehicles()){
                if(vehicle instanceof Car){mobil = vehicle.getBrand();}
                if(vehicle instanceof Motorcyle){motor = vehicle.getBrand();}
            }
            System.out.format(formatTable, number, customer.getName(), customer.getAddress(), member, mobil , motor);

            number++;		// tambah No
            motor = "-";	// reset string motor
            mobil = "-";	// reset string mobil
        }
        System.out.printf(line);
        System.out.println("tekan enter untuk kembali");
        input.nextLine();
    }

	//Booking atau Reservation
	public static void Booking(List<Customer> listAllCustomers){

        Scanner input = new Scanner(System.in);
        System.out.print("Ketik 0 untuk ke menu utama \nMasukan nomor kendaraan : ");
        String vehicleID = input.next();
        for(Customer customers : listAllCustomers){
            for(Vehicle vehicle : customers.getVehicles()){
                if(vehicle.getVehiclesId().contains(vehicleID) && !vehicleID.contains("0")){
                    Service(customers, vehicle.getVehicleType());
                }
            }
        }

        System.out.println("\nKendaraan Tidak Ditemukan");

        if(vehicleID.equals("0")){
            MenuService.mainMenu();
        }
    }

    public static void Service(Customer customer, String type){

        Scanner input = new Scanner(System.in);
        Boolean isMember = false;
        double point = 0.0;
        //member
        if(customer instanceof MemberCustomer){
            isMember = true;
            point = ((MemberCustomer) customer).getSaldoCoin();
        }

        //pisahkan tipe kendaraan kedalam array bary
        List<ItemService> vehicle = new ArrayList<>();
        ItemService service1 = null, service2 = null;

        int num = 1;
        for (ItemService item : listAllItemService) {
            if (type.equalsIgnoreCase(item.getVehicleType())) {
                vehicle.add(item);
                System.out.print(num+". ");
                System.out.println(item.getServiceName());
                num++;
            }
        }

        // menu service
        System.out.print("Masukan menu service : ");

        int index = input.nextInt();
        service1 = vehicle.get(index-1); //list dimulai dari 0

        if(isMember){
            System.out.print("Tambah menu service ? [y/n] : ");
            String addService = input.next();
            if(addService.equals("Y")||addService.equals("y")){
                System.out.print("Masukan menu service : ");
                int index2 = input.nextInt();
                service2 = vehicle.get(index2-1); //list dimulai dari 0
            }
        }


        //pilih metode pembayaran
        int metode = 2; //default metode pembayaran cash

        if(isMember) {
            System.out.println("Metode pembayaran\n1. Poin ("+point+")\n2. Cash");
            System.out.print("Pilih metode Pembayaran : ");
            metode = input.nextInt();
        }

        String formatTable = "| %-15s | %-10s |%n";
        String line = "+-----------------+-------------+%n";
        double harga = 0.0;

        System.out.format(line);
        System.out.format(formatTable, "Nama","Biaya");
        System.out.format(line);
        System.out.format(formatTable, service1.getServiceName(), service1.getPrice());
        harga += service1.getPrice();
        if(metode == 1){
            if(service2 != null){
                System.out.format(formatTable, service2.getServiceName(), service2.getPrice());
                harga +=  service2.getPrice();
            }
            System.out.format(formatTable, "Discount", "10%");
            harga -= harga * .10;
            System.out.format(line);
            System.out.format(formatTable, "Total", harga);
            System.out.format(line);
            System.out.format(formatTable, "Point", point);
            point -= harga;
            System.out.format(formatTable, "Sisa Point", point);
            ((MemberCustomer) customer).setSaldoCoin(point);
        }else{
            System.out.format(formatTable, "Total", harga);
        }
        System.out.format(line);

        System.out.println("Tekan enter untuk kembali");
        Scanner close = new Scanner(System.in);
        close.nextLine();
        MenuService.mainMenu();
    }

	//Top Up Saldo Coin Untuk Member Customer
	public static void TopUp(List<Customer> listAllCustomers){
        Scanner input = new Scanner(System.in);
        System.out.print("\nTop Up Bengkel Coin \nSilahkan masukan ID member anda : ");
        String id = input.nextLine();
        for(Customer customers : listAllCustomers){
            if(id.equals(customers.getCustomerId())){
                if(customers instanceof MemberCustomer){
                    double saldo = ((MemberCustomer) customers).getSaldoCoin();
                    System.out.println("Saldo anda : "+saldo);
                    System.out.print("Masukan jumlah uang : ");
                    double uang = input.nextDouble();
                    double total = uang + saldo;
                    System.out.println("Saldo anda sekarang : "+total);
                    ((MemberCustomer) customers).setSaldoCoin(total);

                    System.out.println("tekan enter untuk kembali ");
                    input.nextLine();
                    input.nextLine();
                    MenuService.mainMenu();
                }else{
                    System.out.println("\nanda bukan member");
                }
            }
        }
        System.out.println("\nID tidak ditemukan");
        TopUp(listAllCustomers);
    }


	//Logout
	
}
