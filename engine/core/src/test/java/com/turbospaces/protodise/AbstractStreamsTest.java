package com.turbospaces.protodise;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turbospaces.demo.Address;
import com.turbospaces.demo.Colors;
import com.turbospaces.demo.User;

public abstract class AbstractStreamsTest {
    
    public static Address a1 = new Address();
    public static Address a2 = new Address();
    public static User u = new User();
    
    public Logger logger = LoggerFactory.getLogger( getClass() );

    static {
        a1.setAddress( "Kiev, some street, 123" );
        a1.setZip( "100423" );
        a1.setCountry( "UKRAINE" );
        a1.setPrimary( true );

        Set<String> details1 = new HashSet<String>();
        details1.add( "detail-l1" );
        details1.add( "detail-l2" );

        Map<String, Long> m = new HashMap<String, Long>();
        m.put( "details-m1", 123L );
        m.put( "details-m3", 321L );

        a1.setDetails1( details1 );
        a1.setDetails2( Arrays.asList( "detail-s1", "details-s2" ) );
        a1.setDetails3( m );
        a1.setColor( Colors.GREEN );

        a2 = (Address) a1.clone();
        a2.setPrimary( false );
        a2.setZip( "9921" );

        u.setFirstName( "x-user-firstname" );
        u.setMiddleName( "x-user-middlename" );
        u.setSecondName( "x-user-secondname" );
        u.setAge( 99 );
        u.setEnabled( true );
        u.setAmount1( 647.27D );
        u.setAmount2( 93.55F );
        u.setTimestamp( System.currentTimeMillis() );
        u.setShortNumber( (short) ( Short.MAX_VALUE / 2 ) );
        u.setOneByte( (byte) 'x' );
        u.setPrimaryAddress( a1 );

        Set<com.turbospaces.demo.Address> unsortedAddresses = new HashSet<Address>();
        unsortedAddresses.add( a1 );
        unsortedAddresses.add( a2 );

        Map<String, com.turbospaces.demo.Address> zip2addresses = new HashMap<String, Address>();
        zip2addresses.put( a1.getZip(), a1 );
        zip2addresses.put( a2.getZip(), a2 );

        Set<String> primitives = new HashSet<String>();
        primitives.add( "s1" );
        primitives.add( "s2" );
        primitives.add( "s3" );
        primitives.add( "s4" );

        u.setUnsortedAddresses( unsortedAddresses );
        u.setSortedAddresses( Arrays.asList( a1, a2 ) );
        u.setZip2addresses( zip2addresses );
        u.setPrimitiveSet( primitives );
    }

    public abstract void address() throws Exception;
    public abstract void user() throws Exception;
}
