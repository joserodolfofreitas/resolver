/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * SecurityActions
 * 
 * A set of privileged actions that are not to leak out of this package
 * 
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 * @version $Revision: $
 */
final class SecurityActions
{

   // -------------------------------------------------------------------------------||
   // Constructor
   // ------------------------------------------------------------------||
   // -------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private SecurityActions()
   {
      throw new UnsupportedOperationException("No instantiation");
   }

   // -------------------------------------------------------------------------------||
   // Utility Methods
   // --------------------------------------------------------------||
   // -------------------------------------------------------------------------------||

   static String getProperty(final String key)
   {
      try
      {
         String value = AccessController.doPrivileged(new PrivilegedExceptionAction<String>()
         {
            public String run()
            {
               return System.getProperty(key);
            }
         });
         return value;
      }
      // Unwrap
      catch (final PrivilegedActionException pae)
      {
         final Throwable t = pae.getCause();
         // Rethrow
         if (t instanceof SecurityException)
         {
            throw (SecurityException) t;
         }
         if (t instanceof NullPointerException)
         {
            throw (NullPointerException) t;
         }
         else if (t instanceof IllegalArgumentException)
         {
            throw (IllegalArgumentException) t;
         }
         else
         {
            // No other checked Exception thrown by System.getProperty
            try
            {
               throw (RuntimeException) t;
            }
            // Just in case we've really messed up
            catch (final ClassCastException cce)
            {
               throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
            }
         }
      }
   }
   /**
    * Obtains the {@link Thread} Context {@link ClassLoader}
    * 
    * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
    */
  static enum GetTcclAction implements PrivilegedAction<ClassLoader>
  {
      INSTANCE;
      @Override
      public ClassLoader run() {
          return Thread.currentThread().getContextClassLoader();
      }

  }
}
