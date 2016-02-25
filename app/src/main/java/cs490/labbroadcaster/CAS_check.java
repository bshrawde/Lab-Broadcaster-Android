/**
 * Created by Brian on 2/25/2016.
 */
//use CAS and or Purdue Directory to check username/password
//using LDAP service for query use
//Server: ped.purdue.edu
//Port 389
//do not specify a search base, LDAP already uses the correct one


package cs490.labbroadcaster;
public class CAS_check{
    private String username="";
    private String password="";
    public CAS_check(String use_name,String pass) {
        this.username = use_name;
        this.password = pass;


    }
}
