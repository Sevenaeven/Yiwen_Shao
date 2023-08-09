from flask import Flask,redirect, url_for,render_template,request,jsonify
import mysql.connector
from flask_bootstrap import Bootstrap

app = Flask(__name__)

app.config['MYSQL_HOST'] = 'localhost'  # Replace with your MySQL server host
app.config['MYSQL_USER'] = 'root'  # Replace with your MySQL username
app.config['MYSQL_PASSWORD'] = '101223!'  # Replace with your MySQL password
app.config['MYSQL_DB'] = 'heart_failure'

bootstrap = Bootstrap(app)

db = mysql.connector.connect(
    host=app.config['MYSQL_HOST'],
    user=app.config['MYSQL_USER'],
    password=app.config['MYSQL_PASSWORD'],
    database=app.config['MYSQL_DB']
)

cursor = db.cursor()

@app.route("/")
def home():
    return render_template('import_sub.html')

@app.route('/importsub')
def importsub():

    importsubject = request.args.get('input')

    # Execute the query using the cursor
    query = "SELECT * FROM patients WHERE SUBJECT_ID = %s"
    cursor.execute(query, (importsubject,))
    patient = cursor.fetchone()

    if patient:
        row_id = patient[0]
        subject_id = patient[1]
        gender = patient[2]
        dob = patient[3]
        dod = patient[4]
        dod_hosp = patient[5]
        dod_ssn = patient[6]
        expire_flag = patient[7]
    else:
        # If no patient is found, set the variables to None
        row_id = None
        subject_id = None
        gender = None
        dob = None
        dod = None
        dod_hosp = None
        dod_ssn = None
        expire_flag = None


    return render_template(
        'import_sub.html',
        row_id = row_id,
        subject_id=subject_id ,
        gender=gender,
        dob=dob,
        dod=dod,
        dod_hosp=dod_hosp,
        dod_ssn=dod_ssn,
        expire_flag=expire_flag)

@app.route('/add/patient')
def add_patient():

    row_id = request.args.get('row_id')
    subject_id = request.args.get('subject_id')
    gender = request.args.get('gender')
    dob = request.args.get('dob')
    dod = request.args.get('dod')
    dod_hosp = request.args.get('dod_hosp')
    dod_ssn = request.args.get('dod_ssn')
    expire_flag = request.args.get('expire_flag')

    if row_id != None:
        query = "INSERT INTO patients (row_id, subject_id, gender, dob, dod, dod_hosp, dod_ssn, expire_flag) VALUES (%s, %s, %s, %s, %s, %s, %s, %s )"
        values = (row_id, subject_id, gender, dob, dod, dod_hosp, dod_ssn, expire_flag)
        cursor.execute(query, values)
        db.commit()  #don't forget it

    return render_template('add.html', row_id = row_id, subject_id=subject_id , gender=gender,dob=dob,dod=dod,dod_hosp=dod_hosp,dod_ssn=dod_ssn,expire_flag=expire_flag)


@app.route('/update/patient')
def update_patient():

    subject_id = request.args.get('subject_id')
    dod = request.args.get('dod')
    dod_hosp = request.args.get('dod_hosp')
    dod_ssn = request.args.get('dod_ssn')
    expire_flag = request.args.get('expire_flag')

    if subject_id != None:
        query = "UPDATE patients SET dod = %s, dod_hosp = %s, dod_ssn = %s, expire_flag = %s WHERE subject_id = %s"
        values = (dod, dod_hosp,dod_ssn, expire_flag, subject_id)
        cursor.execute(query, values)
        db.commit()

    return render_template('update.html', subject_id=subject_id,dod=dod,dod_hosp = dod_hosp, dod_ssn = dod_ssn , expire_flag=expire_flag)

@app.route('/delete/patient')
def delete_patient():

    subject_id = request.args.get('subject_id')

    if subject_id != None:
        query = "DELETE FROM patients WHERE subject_id = %s"
        values = (subject_id,)
        cursor.execute(query, values)
        db.commit()

    return render_template('delete.html',subject_id=subject_id)

@app.route('/filtrate/sex')
def filtrate():
    query = "SELECT * FROM patients WHERE GENDER = %s"
    gender_value = 'F'
    cursor.execute(query, (gender_value,))
    female_patients = cursor.fetchall()

    cof= len(female_patients)

    query = "SELECT * FROM patients WHERE GENDER = %s"
    gender_value = 'M'
    cursor.execute(query, (gender_value,))
    male_patients = cursor.fetchall()

    com = len(male_patients)

    f = '{:.2f}'.format(cof /cof+com)
    m = '{:.2f}'.format(com /cof+com)
    return render_template('filtrate.html',f = f ,m = m ,cof =cof, com =com)

if __name__ == '__main__':
    app.run()