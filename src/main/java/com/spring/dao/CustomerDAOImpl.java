package com.spring.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.entity.Customer;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

	// session factory ekleniyor
	@Autowired
	private SessionFactory sessionFactory;
			
	@Override
	public List<Customer> getCustomers() {
		
		// mevcut hazýrda bekletme oturumunu alýnýyor
		Session currentSession = sessionFactory.getCurrentSession();
				
		// bir sorgu oluþtur ... soyadýna göre sýrala
		Query<Customer> theQuery = 
				currentSession.createQuery("from Customer order by lastName",
											Customer.class);
		
		// Sorguyu çalýþtýr ve sonuç listesini al
		List<Customer> customers = theQuery.getResultList();
				
		// Sonuç döndürülüyor		
		return customers;
	}

	@Override
	public void saveCustomer(Customer theCustomer) {

		// mevcut hazýrda bekletme oturumunu alýnýyor
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Müþteriyi kaydetme ve güncelleme yapýlýyor
		currentSession.saveOrUpdate(theCustomer);
		
	}

	@Override
	public Customer getCustomer(int theId) {

		// mevcut hazýrda bekletme oturumunu alýnýyor
		Session currentSession = sessionFactory.getCurrentSession();
		
		// birincil anahtarý kullanarak veritabanýndan al / okuma yap
		Customer theCustomer = currentSession.get(Customer.class, theId);
		
		return theCustomer;
	}

	@Override
	public void deleteCustomer(int theId) {

		// mevcut hazýrda bekletme oturumunu alýnýyor
		Session currentSession = sessionFactory.getCurrentSession();
		
		// nesneyi birincil anahtarla sil
		Query theQuery = 
				currentSession.createQuery("delete from Customer where id=:customerId");
		theQuery.setParameter("customerId", theId);
		
		theQuery.executeUpdate();		
	}
	
	@Override
	public List<Customer> searchCustomers(String theCustomerName) {
        // mevcut hazýrda bekletme oturumunu al
        Session currentSession = sessionFactory.getCurrentSession();
        
        //Query theQuery = null;
        
            // Ad veya Soyad büyük küçük harflere duyarlý deðil
        Query theQuery =currentSession.createQuery("from Customer where lower(firstName) like :theName or lower(lastName) like :theName or lower(email) like :theName" , Customer.class);
        theQuery.setParameter("theName", "%" + theCustomerName.toLowerCase() + "%");

        
        // Sorguyu çalýþtýr ve sonuç listesi alýnýyor
        List<Customer> customers = theQuery.getResultList();
                
        // Sonucu döndür      
        return customers;
        
	}

}











