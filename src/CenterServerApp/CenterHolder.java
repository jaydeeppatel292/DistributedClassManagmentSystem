package CenterServerApp;

/**
* CenterServerApp/CenterHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Center.idl
* Sunday, 17 June, 2018 9:13:50 PM EDT
*/

public final class CenterHolder implements org.omg.CORBA.portable.Streamable
{
  public CenterServerApp.Center value = null;

  public CenterHolder ()
  {
  }

  public CenterHolder (CenterServerApp.Center initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CenterServerApp.CenterHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CenterServerApp.CenterHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CenterServerApp.CenterHelper.type ();
  }

}
