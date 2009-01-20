import org.hyperic.hq.hqu.rendit.BaseController

import org.hyperic.hq.authz.shared.PermissionException
import org.hyperic.hq.authz.shared.AuthzDuplicateNameException

import org.hyperic.hq.hqapi1.ErrorCode

class RoleController extends ApiController {

    private Closure getRoleXML(r) {
        { doc ->
            Role(id          : r.id,
                 name        : r.name,
                 description : r.description) {
                for (o in r.operations.sort {a, b -> a.name <=> b.name}) {
                    Operation(o.name)
                }
                for (u in r.subjects.sort {a, b -> a.name <=> b.name}) {
                    // TODO: Use getUserXML() in ApiController
                    User(id         : u.id,
                        name        : u.name,
                        firstName   : u.firstName,
                        lastName    : u.lastName,
                        department  : (u.department ? u.department : ''),
                        emailAddress: u.emailAddress,
                        SMSAddress  : (u.SMSAddress ? u.SMSAddress : ''),
                        phoneNumber : (u.phoneNumber ? u.phoneNumber : ''),
                        active      : u.active,
                        htmlEmail   : u.htmlEmail)
                }
            }
        }
    }

    def list(params) {
        renderXml() {
            out << RolesResponse() {
                out << getSuccessXML()
                for (role in roleHelper.allRoles.sort {a, b -> a.name <=> b.name}) {
                    if (!role.system) {
                        out << getRoleXML(role)
                    }
                }
            }
        }
    }

    def get(params) {
        def id   = params.getOne("id")?.toInteger()
        def name = params.getOne("name")

        def r = getRole(id, name)
        renderXml() {
            RoleResponse() {
                if (!r) {
                    out << getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
                } else {
                    out << getSuccessXML()
                    out << getRoleXML(r)
                }
            }
        }
    }

    def create(params) {
        def failureXml
        def createdRole
        try {
            def createRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlRole = createRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    RoleResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def existing = getRole(null, xmlIn.'@name')
            if (existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS)
            } else {
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << o.text()
                }

                def userIds = []
                def subjects = xmlIn['User']
                subjects.each{subj ->
                    def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                    if (u) {
                        log.info("Found user " + u.name)
                        userIds << u.id
                    }
                }
                
                createdRole = roleHelper.createRole(xmlIn.'@name',
                                                    xmlIn.'@description',
                                                    operations as String[],
                                                    userIds as Integer[],
                                                    [] as Integer[])
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            RoleResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                    out << getRoleXML(createdRole)
                }
            }
        }
    }

    def update(params) {
        def failureXml
        try {
            def updateRequest = new XmlParser().parseText(getUpload('postdata'))
            def xmlRole = updateRequest['Role']

            if (!xmlRole || xmlRole.size() != 1) {
                renderXml() {
                    StatusResponse() {
                        out << getFailureXML(ErrorCode.INVALID_PARAMETERS)
                    }
                }
                return
            }

            def xmlIn = xmlRole[0]
            def existing = getRole(xmlIn.'@id'?.toInteger(), xmlIn.'@name')
            if (!existing) {
                failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
            } else {
                def opMap = roleHelper.operationMap
                def operations = []
                def ops = xmlIn['Operation']
                ops.each{o ->
                    operations << opMap[o.text()]
                }

                def users = []
                def subjects = xmlIn['User']
                subjects.each{subj ->
                    def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                    if (u) {
                        log.info("Found user " + u.name)
                        users << u
                    }
                }

                existing.update(user,
                                xmlIn.'@name',
                                xmlIn.'@description')
                existing.setOperations(user, operations)
                existing.setSubjects(user, users)
            }
        } catch (AuthzDuplicateNameException e) {
            log.debug("Duplicate object", e)
            failureXml = getFailureXML(ErrorCode.OBJECT_EXISTS)
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def sync(params) {
        def failureXml
        try {
            def syncRequest = new XmlParser().parseText(getUpload('postdata'))
            for (xmlRole in syncRequest['Role']) {
                def existing = getRole(xmlRole.'@id'?.toInteger(),
                                       xmlRole.'@name')
                if (existing) {
                    def opMap = roleHelper.operationMap
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << opMap[o.text()]
                    }

                    def users = []
                    def subjects = xmlRole['User']
                    subjects.each{subj ->
                        def u = getUser(subj.'@id'?.toInteger(), subj.'@name')
                        if (u) {
                            log.info("Found user " + u.name)
                            users << u
                        }
                    }

                    existing.update(user,
                                    xmlRole.'@name',
                                    xmlRole.'@description')
                    existing.setOperations(user, operations)
                    existing.setSubjects(user, users)
                } else {
                    def operations = []
                    def ops = xmlRole['Operation']
                    ops.each{o ->
                        operations << o.text()
                    }

                    roleHelper.createRole(xmlRole.'@name',
                                          xmlRole.'@description',
                                          operations as String[],
                                          [] as Integer[],
                                          [] as Integer[])
                }
            }
        } catch (PermissionException e) {
            log.debug("Permission denied [${user.name}]", e)
            failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
        } catch (Exception e) {
            log.error("UnexpectedError: " + e.getMessage(), e)
            failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }

    def delete(params) {
        def id = params.getOne('id')?.toInteger()
        def name = params.getOne("name")
        
        def failureXml
        def existing = getRole(id, name)
        if (!existing) {
            failureXml = getFailureXML(ErrorCode.OBJECT_NOT_FOUND)
        } else {
            try {
                existing.remove(user)
            } catch (PermissionException e) {
                log.debug("Permission denied [${user.name}]", e)
                failureXml = getFailureXML(ErrorCode.PERMISSION_DENIED)
            } catch (Exception e) {
                log.error("UnexpectedError: " + e.getMessage(), e)
                failureXml = getFailureXML(ErrorCode.UNEXPECTED_ERROR)
            }
        }

        renderXml() {
            StatusResponse() {
                if (failureXml) {
                    out << failureXml
                } else {
                    out << getSuccessXML()
                }
            }
        }
    }
}