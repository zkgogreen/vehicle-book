package com.bengkel.booking.services;

import com.bengkel.booking.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BengkelService {
	
	//Silahkan tambahkan fitur-fitur utama aplikasi disini
//    private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();

    private static Scanner input = new Scanner(System.in);

	//Login
	public static Customer login(List<Customer> listAllCustomers,String customerID, String password){
        Customer customer = null;
        for(Customer customers : listAllCustomers){
            System.out.println(customers.getName());
            if(customerID.equals(customers.getCustomerId())){
                if(password.equals(customers.getPassword())){
                    customer = customers;
                }else{
                    System.out.println("Password yang anda Masukan Salah!");
                }
                break;
            }
        }
        if(customer == null){

            System.out.println("Customer Id Tidak Ditemukan!");
        }
        return customer;
    }

	//Info Customer
    public static void infoCustomer(List<Customer> listAllCustomers){

        String formatTable = "| %-2s | %-10s | %-15s | %-10s | %-10s | %-15s | %-15s |%n";
        String line = "+----+------------+-----------------+------------+------------+-----------------+-----------------+%n";
        System.out.printf(line);
        System.out.format(formatTable, "No","ID", "name", "Adress","Status","saldo","Mobil", "Motor");
        System.out.printf(line);
        int number = 1;
        String mobil = "-",motor="-";
        for (Customer customer : listAllCustomers) {
            String member = (customer instanceof MemberCustomer)?"Member":"Non Member";
            int saldo = (customer instanceof MemberCustomer)? (int) ((MemberCustomer) customer).getSaldoCoin() :0;
            for(Vehicle vehicle : customer.getVehicles()){
                if(vehicle instanceof Car){mobil = vehicle.getBrand();}
                if(vehicle instanceof Motorcyle){motor = vehicle.getBrand();}
            }
            System.out.format(formatTable, number,customer.getCustomerId() ,customer.getName(), customer.getAddress(), member, saldo, mobil , motor);

            number++;		// tambah No
            motor = "-";	// reset string motor
            mobil = "-";	// reset string mobil
        }
        System.out.printf(line);
        System.out.println("tekan enter untuk kembali");
        input.nextLine();
    }

	//Booking atau Reservation
	public static BookingOrder Booking(List<BookingOrder> listAllBookingOrder, List<Customer> listAllCustomers, List<ItemService> listAllItemService){
        BookingOrder bookingOrder = new BookingOrder();

        System.out.print("Ketik 0 untuk ke menu utama \nMasukan nomor kendaraan : ");
        String vehicleID = input.next();
        for(Customer customer : listAllCustomers){
            for(Vehicle vehicle : customer.getVehicles()){
                if(vehicleID.contains(vehicle.getVehiclesId()) && !vehicleID.contains("0")){
                    bookingOrder.setBookingId("BO-"+(listAllBookingOrder.size()+1));
                    bookingOrder.setCustomer(customer);
                    bookingOrder.setServices(serviceMethod(listAllItemService, customer, vehicle.getVehicleType()));
                    bookingOrder.setTotalServicePrice(TotalPrice(bookingOrder.getServices()));
                    bookingOrder.setPaymentMethod(payment(customer));
                    bookingOrder.setTotalPayment(TotalPayment(bookingOrder, customer));
                    System.out.println("Tekan enter untuk kembali");
                    input.nextLine();
                    input.nextLine();
                    MenuService.mainMenu();
                }
            }
        }

        System.out.println("\nKendaraan Tidak Ditemukan");

        if(vehicleID.equals("0")){
            MenuService.mainMenu();
        }
        return bookingOrder;
    }

    public static List<ItemService> serviceMethod(List<ItemService> listAllItemService, Customer customer, String type){
        List<ItemService> itemServices = new ArrayList<>();
        List<ItemService> vehicle = new ArrayList<>();

        int num = 1;
        for (ItemService item : listAllItemService) {
            if (type.equalsIgnoreCase(item.getVehicleType())) {
                vehicle.add(item);
                System.out.print(num+". ");
                System.out.println(item.getServiceName());
                num++;
            }
        }

        System.out.print("Masukan menu service : ");

        int index = input.nextInt();
        itemServices.add(vehicle.get(index-1)); //list dimulai dari 0

        if(customer instanceof MemberCustomer){
            System.out.print("Tambah menu service ? [y/n] : ");
            String addService = input.next();
            if(addService.equalsIgnoreCase("Y")){
                System.out.print("Masukan menu service : ");
                int index2 = input.nextInt();
                itemServices.add(vehicle.get(index2-1)); //list dimulai dari 0
            }
        }
        return itemServices;
    }

    public static Double TotalPrice(List<ItemService> itemServices){
        double total = 0.0;
        for(ItemService service : itemServices){
            total += service.getPrice();
        }
        return total;
    }

    public static String payment(Customer customer){
        int index = 1;
        String[] paymentMehtod = {"Saldo Coin","Cash"};
        if(customer instanceof MemberCustomer) {
            System.out.print("Pilih metode Pembayaran : \n");
            System.out.println("1. "+paymentMehtod[0]);
            System.out.println("2. "+paymentMehtod[1]);
            index = input.nextInt()-1;
        }
        return paymentMehtod[index];
    }

    public static Double TotalPayment(BookingOrder bookingOrder, Customer customer){
        double harga = 0.0;
        double point = 0.0;
        if(customer instanceof MemberCustomer){
            point = ((MemberCustomer) customer).getSaldoCoin();
        }
        String formatTable = "| %-15s | %-10s |%n";
        String line = "+-----------------+-------------+%n";

        System.out.format(line);
        System.out.format(formatTable, "Nama","Biaya");
        System.out.format(line);
        for(ItemService service : bookingOrder.getServices()){
            System.out.format(formatTable, service.getServiceName(), service.getPrice());
            harga += service.getPrice();
        }
        if(bookingOrder.getPaymentMethod().equalsIgnoreCase("Saldo Coin")){
            System.out.format(formatTable, "Discount", "10%");
            harga -= harga * .1;
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
        return  harga;
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

                }else{
                    System.out.println("\nMaaf fitur ini hanya untuk Member saja!");
                }

                System.out.println("tekan enter untuk kembali ");
                input.nextLine();
                input.nextLine();
                MenuService.mainMenu();
            }
        }
        System.out.println("\nID tidak ditemukan");

        System.out.println("tekan enter untuk kembali ");
        input.nextLine();
        input.nextLine();
        MenuService.mainMenu();
    }

    public static void listBooking(List<BookingOrder> listAllBookingOrder, Customer customer){

        String formatTable = "| %-10s | %-15s | %-30s | %-10s | %-10s | %-10s |%n";
        String line = "+------------+-----------------+--------------------------------+------------+------------+-----------+%n";

        System.out.format(line);
        System.out.format(formatTable, "iD Booking","Customer","Service", "Payment ","Price", "total");
        System.out.format(line);
        for(BookingOrder order : listAllBookingOrder){
            if(customer.equals(order.getCustomer())) {
                String service = "";
                for (ItemService services : order.getServices()) service += services.getServiceName() + ", ";
                System.out.format(formatTable,
                        order.getBookingId(),
                        order.getCustomer().getName(),
                        service,
                        order.getPaymentMethod(),
                        order.getTotalServicePrice(),
                        order.getTotalPayment()
                );
            }
        }
        System.out.format(line);
        System.out.println("Tekan enter untuk kembali");
        input.nextLine();
        input.nextLine();
        MenuService.mainMenu();
    }

	//Logout
	
}
