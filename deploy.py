import paramiko
import os

def run_cmd(ssh, cmd):
    stdin, stdout, stderr = ssh.exec_command(cmd)
    out = stdout.read().decode().strip()
    err = stderr.read().decode().strip()
    return out, err

def deploy_frontend():
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect('47.99.34.251', username='root', password='123456789Ea')

    run_cmd(ssh, 'rm -rf /tmp/frontend-new && mkdir -p /tmp/frontend-new')

    sftp = ssh.open_sftp()
    dist_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'takeout-frontend-v2', 'dist')

    count = 0
    for root, dirs, files in os.walk(dist_dir):
        for f in files:
            local = os.path.join(root, f)
            rel = os.path.relpath(local, dist_dir).replace(os.sep, '/')
            remote = '/tmp/frontend-new/' + rel
            remote_dir = os.path.dirname(remote)
            try:
                sftp.mkdir(remote_dir)
            except:
                pass
            sftp.put(local, remote)
            count += 1
    sftp.close()
    print('Frontend: uploaded ' + str(count) + ' files')

    out, err = run_cmd(ssh, 'docker cp /tmp/frontend-new/. takeout-platform:/usr/share/nginx/html/')
    print('docker cp frontend:', out or err)
    out, err = run_cmd(ssh, 'docker exec takeout-platform nginx -s reload')
    print('nginx reload:', out or err)
    ssh.close()

def deploy_backend():
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect('121.41.228.22', username='root', password='123456789Ea')

    run_cmd(ssh, 'rm -rf /tmp/jars-new && mkdir -p /tmp/jars-new')

    sftp = ssh.open_sftp()
    base = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'takeout-platform')
    services = ['user-service', 'merchant-service', 'order-service', 'payment-service', 'delivery-service', 'search-service', 'notification-service']

    for svc in services:
        local = os.path.join(base, svc, 'target', svc + '-1.0.0-SNAPSHOT.jar')
        remote = '/tmp/jars-new/' + svc + '.jar'
        sftp.put(local, remote)
        print('  uploaded ' + svc)
    sftp.close()

    for svc in services:
        jar_name = svc + '.jar'
        out, err = run_cmd(ssh, 'docker cp /tmp/jars-new/' + jar_name + ' takeout-services:/app/services/' + jar_name)
        print('  docker cp ' + jar_name + ':', out or err)

    out, err = run_cmd(ssh, 'docker restart takeout-services')
    print('restart takeout-services:', out or err)
    ssh.close()

def deploy_gateway():
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect('47.99.34.251', username='root', password='123456789Ea')

    sftp = ssh.open_sftp()
    base = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'takeout-platform')
    local = os.path.join(base, 'gateway', 'target', 'gateway-1.0.0-SNAPSHOT.jar')
    sftp.put(local, '/tmp/gateway.jar')
    sftp.close()
    print('  uploaded gateway')

    out, err = run_cmd(ssh, 'docker cp /tmp/gateway.jar takeout-platform:/app/gateway.jar')
    print('  docker cp gateway:', out or err)
    out, err = run_cmd(ssh, 'docker restart takeout-platform')
    print('  restart takeout-platform:', out or err)
    ssh.close()

print('=== Deploying frontend ===')
deploy_frontend()
print('=== Deploying gateway ===')
deploy_gateway()
print('=== Deploying backend services ===')
deploy_backend()
print('=== Done ===')
