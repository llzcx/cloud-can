#!/bin/bash
function exist_data() {
    echo -e "*** 已安装MySQL或者Mariadb，请先卸载并删除数据目录以及配置文件！ ***\n" && exit
}
if [ -e "/etc/mysql/mysql.cnf" ]; then
    exist_data
elif [ -e "/etc/my.cnf" ]; then
    exist_data
fi
SYSTEM=$(grep -E 'VERSION_ID="7|VERSION_ID="8|VERSION_ID="9|NAME="Debian|NAME="Ubuntu"' /etc/os-release)
version7=$(echo "$SYSTEM" | grep 'VERSION_ID="7')
version8=$(echo "$SYSTEM" | grep 'VERSION_ID="8')
version9=$(echo "$SYSTEM" | grep 'VERSION_ID="9')
version_debian=$(echo "$SYSTEM" | grep 'NAME="Debian')
version_ubuntu=$(echo "$SYSTEM" | grep 'NAME="Ubuntu"')
function get_password() {
    while :; do
        read -p "请输入MySQL密码: " mysql_root_pwd
        mmzw=$(echo "$mysql_root_pwd" | awk '{print gensub(/[!-~]/,"","g",$0)}')
        if [[ ! -n "$mmzw" ]]; then
            break
        else
            echo "请不要输入中文密码！"
        fi
    done
}
function redhat_install_start() {
    echo -e "********* 安装 Mysql8 *********\n"
    sleep 2
    sudo yum -y install mysql-community-server
    systemctl enable mysqld
    systemctl start mysqld
    sleep 5
    echo -e "default-authentication-plugin=mysql_native_password" >>/etc/my.cnf
    echo -e "validate_password.length=1" >>/etc/my.cnf
    echo -e "validate_password.policy=0" >>/etc/my.cnf
    tmp_pwd=$(grep 'temporary password' /var/log/mysqld.log)
    systemctl restart mysqld
    mysql_root_old_pwd=$(echo ${tmp_pwd#*localhost:})
    mysql -uroot --connect-expired-password -p$mysql_root_old_pwd -e "ALTER USER 'root'@'localhost' identified by '$mysql_root_pwd' password expire never;flush privileges;"
    mysql -uroot -p$mysql_root_pwd -e "use mysql;update user set host = '%' where user = 'root' and host='localhost';flush privileges;ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '$mysql_root_pwd' password expire never;flush privileges;"
    firewall-cmd --zone=public --add-port=3306/tcp --permanent
    firewall-cmd --reload
    echo -e "\n********* 安装完成 *********\n"
    echo -e "数据库账号：root\n"
    echo -e "数据库密码：$mysql_root_pwd\n"
}
function debian_install_start() {
    echo -e "********* 安装 Mysql8 *********\n"
    sleep 1
    sudo apt install -y lsb-release ca-certificates apt-transport-https software-properties-common gnupg2
    sudo apt install dirmngr -y
    echo PURGE | sudo debconf-communicate mysql-community-server
    sudo echo "mysql-community-server mysql-community-server/root-pass password $mysql_root_pwd" | sudo debconf-set-selections
    sudo echo "mysql-community-server mysql-community-server/re-root-pass password $mysql_root_pwd" | sudo debconf-set-selections
    sudo echo "mysql-community-server mysql-server/default-auth-override select Use Legacy Authentication Method (Retain MySQL 5.x Compatibility)" | sudo debconf-set-selections
    sudo gpg --keyserver keyserver.ubuntu.com --recv-keys B7B3B788A8D3785C
    sudo gpg --export --armor B7B3B788A8D3785C | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/mysql8.gpg
    sleep 10
    echo "deb http://repo.mysql.com/apt/debian/ $(lsb_release -sc) mysql-8.0" | sudo tee /etc/apt/sources.list.d/mysql80.list
    sudo apt update
    sudo apt install mysql-community-server -y
    mysql -uroot -p$mysql_root_pwd -e "update mysql.user set host = '%' where user = 'root';flush privileges;"
    systemctl restart mysql
    echo -e "\n********* 安装完成 *********\n"
    echo -e "数据库账号：root\n"
    echo -e "数据库密码：$mysql_root_pwd\n"
}
function ubuntu_install_start() {
    echo -e "********* 安装 Mysql8 *********\n"
    sleep 1
    echo PURGE | sudo debconf-communicate mysql-community-server
    sudo echo "mysql-community-server mysql-community-server/root-pass password $mysql_root_pwd" | sudo debconf-set-selections
    sudo echo "mysql-community-server mysql-community-server/re-root-pass password $mysql_root_pwd" | sudo debconf-set-selections
    sudo echo "mysql-community-server mysql-server/default-auth-override select Use Legacy Authentication Method (Retain MySQL 5.x Compatibility)" | sudo debconf-set-selections
    sudo gpg --keyserver keyserver.ubuntu.com --recv-keys B7B3B788A8D3785C
    sudo gpg --export --armor B7B3B788A8D3785C | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/mysql8.gpg
    sleep 10
    echo "deb http://repo.mysql.com/apt/ubuntu $(lsb_release -sc) mysql-8.0" | sudo tee /etc/apt/sources.list.d/mysql80.list
    sudo apt update
    sudo apt install mysql-community-server -y
    mysql -uroot -p$mysql_root_pwd -e "update mysql.user set host = '%' where user = 'root';flush privileges;"
    systemctl restart mysql
    sleep 5
    echo -e "\n********* 安装完成 *********\n"
    echo -e "数据库账号：root\n"
    echo -e "数据库密码：$mysql_root_pwd\n"
}

if [[ -n "$version7" ]]; then
    echo -e " *********此系统为Redhat 7系列操作系统发行版 *********\n"
    sudo yum -y install https://repo.mysql.com/mysql80-community-release-el7-7.noarch.rpm
    get_password
    redhat_install_start
elif [[ -n "$version8" ]]; then
    echo -e "********* 此系统为Redhat 8系列操作系统发行版 *********\n"
    sudo dnf -y module disable mysql
    sudo dnf -y install yum
    sudo dnf -y install https://repo.mysql.com/mysql80-community-release-el8-4.noarch.rpm
    get_password
    redhat_install_start
elif [[ -n "$version9" ]]; then
    echo -e "********* 此系统为Redhat 9系列操作系统发行版 *********\n"
    sudo dnf -y install yum
    sudo dnf -y install https://repo.mysql.com/mysql80-community-release-el9-1.noarch.rpm
    get_password
    redhat_install_start
elif [[ -n "$version_debian" ]]; then
    echo -e " *********此系统为Debian系列操作系统发行版 *********\n"
    apt update
    apt install sudo gawk -y
    get_password
    debian_install_start
elif [[ -n "$version_ubuntu" ]]; then
    echo -e " *********此系统为Ubuntu系列操作系统发行版 *********\n"
    apt update
    sudo apt install dirmngr gawk -y
    get_password
    ubuntu_install_start
else
    echo -e "********* 未支持的系统 *********\n" && exit
fi