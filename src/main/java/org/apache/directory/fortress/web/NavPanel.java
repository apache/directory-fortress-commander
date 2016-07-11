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

package org.apache.directory.fortress.web;


import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 *          Date: 5/21/13
 */
public class NavPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;


    public NavPanel( String id )
    {
        super( id );
        add( new NavForm( "navButtons" ) );
        this.setOutputMarkupId( true );
    }

    public class NavForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;


        public NavForm( String id )
        {
            super( id );
            add( new SecureIndicatingAjaxButton( GlobalIds.USERS_PAGE, GlobalIds.ROLE_USERS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( UserPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );

            add( new SecureIndicatingAjaxButton( GlobalIds.ROLES_PAGE, GlobalIds.ROLE_ROLES )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( RolePage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );
            final PageParameters parameters = new PageParameters();

            add( new SecureIndicatingAjaxButton( GlobalIds.POBJS_PAGE, GlobalIds.ROLE_PERMOBJS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( ObjectPage.class, parameters );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.PERMS_PAGE, GlobalIds.ROLE_PERMS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( PermPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.PWPOLICIES_PAGE, GlobalIds.ROLE_POLICIES )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( PwPolicyPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.SSDS_PAGE, GlobalIds.ROLE_SSDS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( SdStaticPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.DSDS_PAGE, GlobalIds.ROLE_DSDS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( SdDynamicPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.USEROUS_PAGE, GlobalIds.ROLE_USEROUS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( OuUserPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.PERMOUS_PAGE, GlobalIds.ROLE_PERMOUS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( OuPermPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.ADMROLES_PAGE, GlobalIds.ROLE_ADMINROLES )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( RoleAdminPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.ADMPOBJS_PAGE, GlobalIds.ROLE_ADMINOBJS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( ObjectAdminPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.ADMPERMS_PAGE, GlobalIds.ROLE_ADMINPERMS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( PermAdminPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            /*
            TODO: Add Group Back (replace datatable)
            add( new SecureIndicatingAjaxButton( GlobalIds.GROUP_PAGE, GlobalIds.ROLE_GROUPS )
            {
                */
                /** Default serialVersionUID *//*

                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( GroupPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        */
                        /** Default serialVersionUID *//*

                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );
            */

            add( new SecureIndicatingAjaxButton( GlobalIds.AUDIT_BINDS_PAGE, GlobalIds.ROLE_AUDIT_BINDS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( AuditBindPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.AUDIT_AUTHZS_PAGE, GlobalIds.ROLE_AUDIT_AUTHZS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( AuditAuthzPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            add( new SecureIndicatingAjaxButton( GlobalIds.AUDIT_MODS_PAGE, GlobalIds.ROLE_AUDIT_MODS )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( AuditModPage.class );
                }


                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        /** Default serialVersionUID */
                        private static final long serialVersionUID = 1L;


                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            }

            );

            //@Authorizable
            /*
                        add( new SecureIndicatingAjaxButton( "test", GlobalIds.ADMIN_MGR, "test")
                        {
                            @Override
                            public void onSubmit( AjaxRequestTarget target, Form<?> form )
                            {
                                // sleep for 5 seconds to show off the busy indicator
                                try
                                {
                                    Thread.sleep( 5000 );
                                }
                                catch ( InterruptedException e )
                                {
                                    // noop
                                }
                            }
                        }.setPosition( IndicatingAjaxButton.Position.RIGHT));
            */
            /*
                    add( new SecureIndicatingAjaxButton("test" )
                    {
                        @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
                        {
                            // sleep for 5 seconds to show off the busy indicator
                            try
                            {
                                TestAuthorization testSecurity = new TestAuthorization();
                                boolean result = testSecurity.checkAccess( GlobalUtils.getSession( this ), "foo", "fighters" );
                                Thread.sleep( 1000 );
                            }
                            catch ( InterruptedException e )
                            {
                                // noop
                            }
                        }
                    }.setPosition( IndicatingAjaxButton.Position.RIGHT )
                    );
            */
        }
    }
}