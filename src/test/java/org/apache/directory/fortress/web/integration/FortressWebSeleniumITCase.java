/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.fortress.web.integration;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.LocalFileDetector;
import org.apache.directory.fortress.web.common.GlobalIds;

/**
 * This class uses apache selenium firefox driver to drive commander web ui
 */
public class FortressWebSeleniumITCase
{
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    private static final Logger log = Logger.getLogger( FortressWebSeleniumITCase.class.getName() );

    @Before
    public void setUp() throws Exception
    {
        FirefoxProfile ffProfile = new FirefoxProfile();
        ffProfile.setPreference( "browser.safebrowsing.malware.enabled", false );
        driver = new FirefoxDriver( ffProfile );
        driver.manage().window().maximize();

        // tomcat default:
        baseUrl = "http://localhost:8080";
        //baseUrl = "http://fortressdemo2.com:8080";
        // tomcat SSL:
        //baseUrl = "https://localhost:8443";
        //baseUrl = "https://fortressdemo2.com:8443";
        driver.manage().timeouts().implicitlyWait( 5, TimeUnit.SECONDS );
    }

    @Test
    public void testCase1() throws Exception
    {
        log.info( "Begin FortressWebSeleniumITCase" );
        driver.get( baseUrl + "/fortress-web" );
        login();
        TUtils.sleep( 1 );

        boolean skipFirstHalf = false;
        //boolean skipFirstHalf = true;
        boolean skipSecondHalf = false;
        //boolean skipSecondHalf = true;
        if ( !skipFirstHalf )
        {
            users();
            roles();
            pobjs();
            perms();
            ssds();
            dsds();
            ouusers();
            ouperms();
        }

        if ( !skipSecondHalf )
        {
            admrles();
            admobjs();
            admperms();
            //plcys();
            //groups();
            //binds();
            //authzs();
            //mods();
        }


        /*****
         *  LOGOUT
         */
        driver.findElement( By.linkText( "LOGOUT" ) ).click();
        log.info( "End FortressWebSeleniumITCase" );
        //driver.findElement( By.linkText( "glob:search*" ) ).click();
    }

    private void login()
    {
        driver.findElement( By.id( GlobalIds.USER_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).sendKeys( "test" );
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).clear();
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).sendKeys( "password" );
        driver.findElement( By.name( GlobalIds.LOGIN ) ).click();
    }

    private void users()
    {
        /*****
         *  USERS_PAGE TESTS
         */
        driver.findElement( By.linkText( "USERS" ) ).click();
        //driver.findElement( By.id( "roleRb" ) ).click();
        driver.findElement( By.id( "roleAssignLinkLbl" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( "userformsearchfields:" + GlobalIds.SEARCH ) ).click();
        driver.findElement( By.id( GlobalIds.FIELD_1 ) ).clear();
        driver.findElement( By.id( GlobalIds.FIELD_1 ) ).sendKeys( "dev1" );
        driver.findElement( By.id( "ouAssignLinkLbl" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( "userformsearchfields:" + GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
        WebElement table = driver.findElement(By.id("usertreegrid"));
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        allRows.get( 4 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 5 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.CLEAR ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.USER_ID ) ).sendKeys( "selTestU1" );
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).clear();
        driver.findElement( By.id( GlobalIds.PSWD_FIELD ) ).sendKeys( "password" );
        driver.findElement( By.id( GlobalIds.OU ) ).clear();
        driver.findElement( By.id( GlobalIds.OU ) ).sendKeys( "dev1" );
        driver.findElement( By.name( GlobalIds.OU_SEARCH ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 1 );
/*
TODO: FIX ME:
        driver.findElement( By.name( GlobalIds.POLICY_SEARCH ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 1 );
*/
        driver.findElement( By.name( GlobalIds.ADD ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.ROLE_ASSIGNMENTS_LABEL ) ).click();
        //( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('roles')).val('role1');" );
        driver.findElement( By.id( GlobalIds.ASSIGN_NEW_ROLE ) ).sendKeys( "ROLE_USERS" );
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_RC ) ).sendKeys( "8:00 AM" );
        driver.findElement( By.id( GlobalIds.END_TIME_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_TIME_RC ) ).sendKeys( "5:00 PM" );
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_RC ) ).sendKeys( "1/1/2013" );
        driver.findElement( By.id( GlobalIds.END_DATE_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_DATE_RC ) ).sendKeys( "1/1/2099" );
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_RC ) ).sendKeys( "6/1/2013" );
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_RC ) ).sendKeys( "6/15/2013" );
        driver.findElement( By.id( GlobalIds.TIMEOUT_RC ) ).clear();
        driver.findElement( By.id( GlobalIds.TIMEOUT_RC ) ).sendKeys( "120" );
        driver.findElement( By.id( GlobalIds.SUNDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.MONDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.TUESDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.WEDNESDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.THURSDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.FRIDAY_RC ) ).click();
        driver.findElement( By.id( GlobalIds.SATURDAY_RC ) ).click();
        driver.findElement( By.name( GlobalIds.ASSIGN ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.ROLE_ASSIGNMENTS_LABEL ) ).click();
        driver.findElement( By.id( GlobalIds.ASSIGN_NEW_ROLE ) ).clear();
        TUtils.sleep( 2 );
        driver.findElement( By.name( GlobalIds.ROLES_SEARCH ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( "3" ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.name( GlobalIds.ASSIGN ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.id( "adminRoleAssignmentsLabel" ) ).click();
            /*
                    if(driver.findElement( By.name( "adminRoles" ) ).isDisplayed())
                    {
                        System.out.println("adminRoles is displayed!!!");
                    }
                    else
                    {
                        System.out.println("adminRoles is NOT displayed!!!");
                    }
            */
        //( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('adminRoles')).val" +
        //    "('DemoAdminUsers');" );
        driver.findElement( By.id( GlobalIds.ASSIGN_NEW_ADMIN_ROLE ) ).sendKeys( "o" );
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_ARC ) ).sendKeys( "8:00 AM" );
        driver.findElement( By.id( GlobalIds.END_TIME_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_TIME_ARC ) ).sendKeys( "5:00 PM" );
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_ARC ) ).sendKeys( "1/1/2013" );
        driver.findElement( By.id( GlobalIds.END_DATE_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_DATE_ARC ) ).sendKeys( "1/1/2099" );
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_ARC ) ).sendKeys( "6/1/2013" );
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_ARC ) ).sendKeys( "6/15/2013" );
        driver.findElement( By.id( GlobalIds.TIMEOUT_ARC ) ).clear();
        driver.findElement( By.id( GlobalIds.TIMEOUT_ARC ) ).sendKeys( "180" );
        driver.findElement( By.id( GlobalIds.MONDAY_ARC ) ).click();
        driver.findElement( By.id( GlobalIds.TUESDAY_ARC ) ).click();
        driver.findElement( By.id( GlobalIds.WEDNESDAY_ARC ) ).click();
        driver.findElement( By.id( GlobalIds.THURSDAY_ARC ) ).click();
        driver.findElement( By.id( GlobalIds.FRIDAY_ARC ) ).click();
        driver.findElement( By.name( GlobalIds.ASSIGN_ADMIN_ROLE ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( "adminRoleAssignmentsLabel" ) ).click();
        driver.findElement( By.name( "adminRoles.search" ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.name( GlobalIds.ASSIGN_ADMIN_ROLE ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.id( "contactInformationLabel" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).clear();
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).sendKeys( "Selenium Test User" );
        driver.findElement( By.id( GlobalIds.EMPLOYEE_TYPE ) ).clear();
        driver.findElement( By.id( GlobalIds.EMPLOYEE_TYPE ) ).sendKeys( "Test User" );
        driver.findElement( By.id( GlobalIds.TITLE ) ).clear();
        driver.findElement( By.id( GlobalIds.TITLE ) ).sendKeys( "TestUser" );
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('emails')).show();" );
        driver.findElement( By.id( GlobalIds.EMAILS ) ).clear();
        driver.findElement( By.id( GlobalIds.EMAILS ) ).sendKeys( "joeuser@selenium.com" );
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('phones')).show();" );
        driver.findElement( By.id( GlobalIds.PHONES ) ).clear();
        driver.findElement( By.id( GlobalIds.PHONES ) ).sendKeys( "555-555-5555" );
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('mobiles')).show();" );
        driver.findElement( By.id( GlobalIds.MOBILES ) ).clear();
        driver.findElement( By.id( GlobalIds.MOBILES ) ).sendKeys( "222-222-2222" );
        driver.findElement( By.id( GlobalIds.ADDRESS_ASSIGNMENTS_LABEL ) ).click();
        TUtils.sleep( 1 );
        ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('addresses')).show();" );
        driver.findElement( By.id( GlobalIds.ADDRESSES ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESSES ) ).sendKeys( "9 Manor Road" );
        driver.findElement( By.id( GlobalIds.ADDRESS_CITY ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_CITY ) ).sendKeys( "Salina" );
        driver.findElement( By.id( GlobalIds.ADDRESS_STATE ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_STATE ) ).sendKeys( "KS" );
        driver.findElement( By.id( GlobalIds.ADDRESS_COUNTRY ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_COUNTRY ) ).sendKeys( "US" );
        driver.findElement( By.id( GlobalIds.ADDRESS_POSTAL_CODE ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_POSTAL_CODE ) ).sendKeys( "67401" );
        driver.findElement( By.id( GlobalIds.ADDRESS_POST_OFFICE_BOX ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_POST_OFFICE_BOX ) ).sendKeys( "422" );
        driver.findElement( By.id( GlobalIds.ADDRESS_BUILDING ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_BUILDING ) ).sendKeys( "2929" );
        driver.findElement( By.id( GlobalIds.ADDRESS_DEPARTMENT_NUMBER ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_DEPARTMENT_NUMBER ) ).sendKeys( "2222" );
        driver.findElement( By.id( GlobalIds.ADDRESS_ROOM_NUMBER ) ).clear();
        driver.findElement( By.id( GlobalIds.ADDRESS_ROOM_NUMBER ) ).sendKeys( "555" );
        driver.findElement( By.id( GlobalIds.TEMPORAL_CONSTRAINTS_LABEL ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).sendKeys( "8:00 AM" );
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).sendKeys( "5:00 PM" );
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).sendKeys( "1/1/2013" );
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).sendKeys( "1/1/2099" );
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).sendKeys( "6/1/2013" );
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).sendKeys( "6/15/2013" );
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).clear();
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).sendKeys( "0" );
        driver.findElement( By.id( GlobalIds.SUNDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.MONDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.TUESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.WEDNESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.THURSDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.FRIDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.SATURDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.SYSTEM_INFO_LABEL ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.SYSTEM ) ).click();
        driver.findElement( By.id( GlobalIds.CN ) ).clear();
        driver.findElement( By.id( GlobalIds.CN ) ).sendKeys( "Firstname Lastname" );
        driver.findElement( By.id( GlobalIds.SN ) ).clear();
        driver.findElement( By.id( GlobalIds.SN ) ).sendKeys( "Lastname" );
        driver.findElement( By.id( GlobalIds.IMPORT_PHOTO_LABEL ) ).click();
        TUtils.sleep( 1 );
        WebElement element = driver.findElement( By.name( "upload" ) );
        LocalFileDetector detector = new LocalFileDetector();
        String path = "./src/test/resources/p1.jpeg";
        File f = detector.getLocalFile( path );
        element.sendKeys( f.getAbsolutePath() );
        driver.findElement( By.name( GlobalIds.SAVE ) ).click();
        TUtils.sleep( 3 );
        driver.findElement( By.name( GlobalIds.COMMIT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.IMPORT_PHOTO_LABEL ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.name( GlobalIds.DELETE ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.SYSTEM_INFO_LABEL ) ).click();
        driver.findElement( By.id( GlobalIds.SYSTEM ) ).click();
        driver.findElement( By.name( GlobalIds.COMMIT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.DELETE ) ).click();
        TUtils.sleep( 1 );
    }

    private void roles()
    {
        driver.findElement( By.linkText( "ROLES" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).clear();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "oamt13" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        driver.findElement( By.id( GlobalIds.NAME ) ).clear();
        driver.findElement( By.id( GlobalIds.NAME ) ).sendKeys( "SelTestRole" );
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).clear();
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).sendKeys( "Selenium Test Role" );
        driver.findElement( By.name( GlobalIds.PARENTROLES_SEARCH ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.name( GlobalIds.ADD ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.TEMPORAL_CONSTRAINTS_LABEL ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).sendKeys( "8:00 AM" );
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).sendKeys( "5:00 PM" );
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).sendKeys( "1/1/2013" );
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).sendKeys( "1/1/2099" );
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).sendKeys( "6/1/2013" );
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).sendKeys( "6/15/2013" );
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).clear();
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).sendKeys( "0" );
        driver.findElement( By.id( GlobalIds.SUNDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.MONDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.TUESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.WEDNESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.THURSDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.FRIDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.SATURDAY_P ) ).click();
        driver.findElement( By.name( GlobalIds.COMMIT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.DELETE ) ).click();
        TUtils.sleep( 1 );
    }

    private void pobjs()
    {
        driver.findElement( By.linkText( "POBJS" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "t" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void perms()
    {
        driver.findElement( By.linkText( "PERMS" ) ).click();
        driver.findElement( By.id( "permObject" ) ).sendKeys( "/cal" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void ssds()
    {
        driver.findElement( By.linkText( "SSDS" ) ).click();
        driver.findElement( By.id( "roleRb" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "oamT16SDR6" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void dsds()
    {
        driver.findElement( By.linkText( "DSDS" ) ).click();
        driver.findElement( By.id( "roleRb" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "oamT13DSD6" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void ouusers()
    {
        driver.findElement( By.linkText( "OUSERS" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "d" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void ouperms()
    {
        driver.findElement( By.linkText( "OUPRMS" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "a" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
    }

    private void admrles()
    {
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( "ADMRLES" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).clear();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "t" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        driver.findElement( By.id( GlobalIds.NAME ) ).clear();
        driver.findElement( By.id( GlobalIds.NAME ) ).sendKeys( "SelTestAdminRole" );
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).clear();
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).sendKeys( "Selenium Test Admin Role" );
        driver.findElement( By.name( GlobalIds.PARENTROLES_SEARCH ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 2 );
        // DELEGATION DETAILS:
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.USEROU_SEARCH ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.PERMOU_SEARCH ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.id( GlobalIds.BEGIN_RANGE ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_RANGE ) ).sendKeys( "oamT6D" );
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.BEGIN_RANGE_SEARCH ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.BEGIN_INCLUSIVE ) ).click();
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.END_RANGE_SEARCH ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.name( GlobalIds.ROLEAUXPANEL + ":" + GlobalIds.END_INCLUSIVE ) ).click();
        driver.findElement( By.name( GlobalIds.ADD ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.TEMPORAL_CONSTRAINTS_LABEL ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_TIME_P ) ).sendKeys( "8:00 AM" );
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_TIME_P ) ).sendKeys( "5:00 PM" );
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_DATE_P ) ).sendKeys( "1/1/2013" );
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_DATE_P ) ).sendKeys( "1/1/2099" );
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.BEGIN_LOCK_DATE_P ) ).sendKeys( "6/1/2013" );
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).clear();
        driver.findElement( By.id( GlobalIds.END_LOCK_DATE_P ) ).sendKeys( "6/15/2013" );
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).clear();
        driver.findElement( By.id( GlobalIds.TIMEOUT_P ) ).sendKeys( "0" );
        driver.findElement( By.id( GlobalIds.SUNDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.MONDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.TUESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.WEDNESDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.THURSDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.FRIDAY_P ) ).click();
        driver.findElement( By.id( GlobalIds.SATURDAY_P ) ).click();
        driver.findElement( By.name( GlobalIds.COMMIT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.DELETE ) ).click();
        TUtils.sleep( 1 );
    }

    private void admobjs()
    {
        driver.findElement( By.linkText( "ADMOBJS" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "u" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void admperms()
    {
        driver.findElement( By.linkText( "ADMPERMS" ) ).click();
        driver.findElement( By.id( "objectAssignLinkLbl" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void plcys()
    {
        driver.findElement( By.linkText( "PLCYS" ) ).click();
        driver.findElement( By.id( GlobalIds.SEARCH_VAL ) ).sendKeys( "oamtp1policy" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
    }

    private void groups()
    {
        driver.findElement( By.linkText( "GROUPS" ) ).click();
        driver.findElement( By.id( "searchVal" ) ).sendKeys( "t" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
        TUtils.sleep( 1 );
        WebElement table = driver.findElement(By.id("grouptreegrid"));
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        allRows.get( 4 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 5 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.CLEAR ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.id( GlobalIds.NAME ) ).sendKeys( "selGroup1" );
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).clear();
        driver.findElement( By.id( GlobalIds.DESCRIPTION ) ).sendKeys( "Selenium Test Create Group Node" );
        driver.findElement( By.id( "protocol" ) ).clear();
        driver.findElement( By.id( "protocol" ) ).sendKeys( "test" );
        driver.findElement( By.id( "memberProps" ) ).clear();
        driver.findElement( By.id( "memberProps" ) ).sendKeys( "testKey1=testVal1" );

        driver.findElement( By.name( "members.search" ) ).click();
        TUtils.sleep( 2 );
        driver.findElement( By.linkText( ">" ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        TUtils.sleep( 1 );
        driver.findElement( By.name( GlobalIds.ADD ) ).click();
        TUtils.sleep( 1 );
    }

    private void binds()
    {
        driver.findElement( By.linkText( "BINDS" ) ).click();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).sendKeys( "jtsuser1" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
        WebElement table = driver.findElement(By.id("bindtreegrid"));
        // Now get all the TR elements from the table
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        // And iterate over them, getting the cells
        allRows.get( 4 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 5 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
    }

    private void authzs()
    {
        driver.findElement( By.linkText( "AUTHZ" ) ).click();
        driver.findElement( By.id( GlobalIds.OBJ_NAME ) ).clear();
        driver.findElement( By.id( GlobalIds.OBJ_NAME ) ).sendKeys( "org.apache.directory.fortress.core.impl.AdminMgrImpl" );
        driver.findElement( By.name( "admin" ) ).click();
        driver.findElement( By.id( "permLinkLbl" ) ).click();
        TUtils.sleep( 1 );
        //driver.findElement( By.linkText( "6" ) ).click();
        driver.findElement( By.linkText( GlobalIds.SELECT ) ).click();
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 1 );
        WebElement table = driver.findElement(By.id("authztreegrid"));
        // Now get all the TR elements from the table
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        // And iterate over them, getting the cells
        allRows.get( 4 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 5 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
    }

    private void mods()
    {
        driver.findElement( By.linkText( "MODS" ) ).click();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).clear();
        driver.findElement( By.id( GlobalIds.USER_ID ) ).sendKeys( "test" );
        driver.findElement( By.name( GlobalIds.SEARCH ) ).click();
        TUtils.sleep( 5 );
        WebElement table = driver.findElement(By.id("modtreegrid"));
        // Now get all the TR elements from the table
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        // And iterate over them, getting the cells
        allRows.get( 5 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        nextPage( "modstable");
        TUtils.sleep( 1 );
        allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
        TUtils.sleep( 1 );
        nextPage( "modstable");
        TUtils.sleep( 1 );
    }

    private void nextPage(String szTableName)
    {
        WebElement table = driver.findElement( By.id( szTableName ) );
        List<WebElement> allRows = table.findElements( By.tagName( "a" ) );
        for (WebElement row : allRows)
        {
            String szText = row.getText();
            if(szText.equals( "Go to the next page" ))
                row.click();
            log.debug( "row text=" + row.getText());
        }
    }

    @After
    public void tearDown() throws Exception
    {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if ( !"".equals( verificationErrorString ) )
        {
            fail( verificationErrorString );
        }
    }

    private boolean isElementPresent( By by )
    {
        try
        {
            driver.findElement( by );
            return true;
        }
        catch ( NoSuchElementException e )
        {
            return false;
        }
    }

    private boolean isAlertPresent()
    {
        try
        {
            driver.switchTo().alert();
            return true;
        }
        catch ( NoAlertPresentException e )
        {
            return false;
        }
    }

    private String closeAlertAndGetItsText()
    {
        try
        {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if ( acceptNextAlert )
            {
                alert.accept();
            }
            else
            {
                alert.dismiss();
            }
            return alertText;
        }
        finally
        {
            acceptNextAlert = true;
        }
    }

    // Warning: verifyTextPresent may require manual changes
        /*
                try
                {
                    assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Commander Web
                    Admin[\\s\\S]*$"));
                }
                catch ( Error e )
                {
                    verificationErrors.append( e.toString() );
                }
        */
    // Warning: verifyTextPresent may require manual changes
        /*
                try
                {
                    assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches
                    ("^[\\s\\S]*UserAdministration[\\s\\S]*$"));
                }
                catch ( Error e )
                {
                    verificationErrors.append( e.toString() );
                }
                // Warning: verifyTextPresent may require manual changes
                try
                {
                    assertTrue( driver.findElement( By.cssSelector( "BODY" ) ).getText().matches(
                        "^[\\s\\S]*jtsTU16User7[\\s\\S]*$" ) );
                }
                catch ( Error e )
                {
                    verificationErrors.append( e.toString() );
                }
        */
/*
            table = driver.findElement(By.id( "modstable"));
            allRows = table.findElements(By.tagName("a"));
            for (WebElement row : allRows)
            {
                String szText = row.getText();
                if(szText.equals( "Go to the next page" ))
                    row.click();
                log.info( "row text=" + row.getText());
            }
            TUtils.sleep( 1 );
*/
            //table = driver.findElement(By.className( "k-link"));
            //driver.findElement( By.className( "k-link" ) ).click();
            //driver.findElement( By.className( "k-icon k-i-arrow-e" ) ).click();
            //a.k-link:nth-child(4) > span:nth-child(1)
            //TUtils.sleep( 1 );
            //driver.findElement( By.className( "k-link" ) ).click();
            //TUtils.sleep( 1 );
            //driver.findElement( By.className( "k-link" ) ).click();
            //TUtils.sleep( 1 );
/*
            allRows.get( 6 ).findElement(By.className("imxt-cell")).click();
            TUtils.sleep( 1 );
            allRows.get( 7 ).findElement(By.className("imxt-cell")).click();
*/
    /*
                for(WebElement row : allRows)
                {
                    if(rowctr++ < 5)
                        continue;

                    row.findElement(By.className("imxt-cell")).click();
                    TUtils.sleep( 3 );
                    //driver.findElement(By.className("imxt-cell")).click();
                }
    */
    /*
                for (WebElement row : allRows)
                {
                    rowctr++;
                    //List<WebElement> cells = row.findElements(By.tagName("td"));

                    if(rowctr < 5)
                        continue;

                    List<WebElement> cells = row.findElements(By.className("imxt-cell"));
                    driver.findElement(By.className("imxt-cell")).click();
    */
                    //List<WebElement> cells = row.findElements(By.className("imxt-cell"));

                    //List<WebElement> cells = row.findElements(By.className("imxt-a-imxt-nowrap"));
                    //*[@id="body35_1"]/td[1]/div
    /*
                    int cellctr = 0;
                    for (WebElement cell : cells)
                    {
                        cellctr++;
                        //log.info( "Cell[" + rowctr + "][" + cellctr + "]: " + cell.toString() );
                        //log.info( "cell tagname: " + cell.getTagName());
                        //String td = cell.getAttribute( "td" );
                        String td = cell.getText();
                        //String innerText = driver.findElement(By.xpath(".//div")).getText();
                        //String innerText = driver.findElement(By.className("imxt-cell")).getText();
                        log.info( "intext[" + rowctr + "][" + cellctr + "] td value:" + td);

                        //List<WebElement> cells2 = cell.findElements(By.className("imxt-cell"));
                        // "imxt-cell"
                        ///*/
    /*[@id="body35_1"]/td[1]
                     }
    */
                    //List<WebElement> cells = row.findElements(By.xpath(".//*[@id=\"*\"]/td[2]/div"));
                    //List<WebElement> cells = row.findElements(By.xpath("//table/tbody/tr[" + rowctr + "/td[1]"));
                    //String innerText = driver.findElement(By.xpath("//table/tbody/tr[" + rowctr + "]/td[1]")).getText();
                    //List<WebElement> cells = row.findElements(By.xpath(".//td[1]"));
                    //log.info( "intext[" + rowctr + ":" + innerText);

                    ////*[@id="body35_9"]
                    //<div class="imxt-a imxt-nowrap">demoUser10</div>
    /*
                    int cellctr = 0;
                    for (WebElement cell : cells)
                    {
                        cellctr++;
                        log.info( "Cell[" + rowctr + "][" + cellctr + "]: " + cell.toString() );
                        log.info( "cell tagname: " + cell.getTagName());
                     }
    */
    //            }

                //List<WebElement> cells = row.findElements(By.xpath(".//*[local-name(.)='th' or local-name(.)='td']"));
                //TUtils.sleep( 1 );
                //( ( JavascriptExecutor ) driver ).executeScript( "document.getElementById('usertreegrid').focus();" );
    /*
                ( ( JavascriptExecutor ) driver ).executeScript( "$(document.getElementById('adminRoles')).val" +
                    "('DemoAdminUsers');" );
    */
}
