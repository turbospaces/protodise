package com.turbospaces.protodise;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.turbospaces.demo.Address;
import com.turbospaces.demo.Colors;
import com.turbospaces.demo.User;

public abstract class AbstractStreamsTest {
    public static Address a1 = new Address();
    public static Address a2 = new Address();
    public static User u = new User();

    static {
        a1.setAddress( "Kiev, some street, 123" );
        a1.setZip( "100423" );
        a1.setCountry( "UKRAINE" );
        a1.setPrimary( true );
        a1.setDetails1( ImmutableSet.of( "detail-l1", "details-l2" ) );
        a1.setDetails2( ImmutableList.of( "detail-s1", "details-s2" ) );
        a1.setDetails3( ImmutableMap.of( "details-m1", 123L, "details-m3", 321L ) );
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
        u.setUnsortedAddresses( ImmutableSet.of( a1, a2 ) );
        u.setSortedAddresses( ImmutableList.of( a1, a2 ) );
        u.setZip2addresses( ImmutableMap.of( a1.getZip(), a1, a2.getZip(), a2 ) );
        u.setPrimitiveSet( ImmutableSet.of( "s1", "s2", "s3", "s4" ) );
    }

    public abstract void address() throws Exception;
    public abstract void user() throws Exception;
}