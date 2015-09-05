REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'nitrogen_user'@'%','nitrogen_user'@'localhost','nitrogen_user'@'127.0.0.1';
FLUSH PRIVILEGES;

DROP USER 'nitrogen_user'@'%','nitrogen_user'@'localhost','nitrogen_user'@'127.0.0.1';