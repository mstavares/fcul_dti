pragma solidity ^0.4.21;

// grades  0 nao inscrito
// grades -1 nao avaliado
// grades -2 reavaliação

contract AcademicService {
    
    /**
     * Esta estrutura cria uma abstração relativa à entidade curso
     */ 
    struct Course {
        uint8 credits;
        address professor;
        mapping(address => int) grades;
    }
    
    /**
     * Esta estrutura cria uma abstração relativa à entidade estudante
     */ 
    struct Student {
        address student;
        uint8 registeredCredits;
        uint8 approvedCredits;
    }
    
    /** Esta variavel guarda o endereço da escola */
    address public school;
    
    /** Esta variabel guarda o momento em que o curso começou */
    uint256 public start;
    
    /** Este array guarda o conjunto de cursos criados pela escola */
    Course[]public courses;
    
    /** Este mapping criam uma relação entre o endereço e um estudante */
    mapping(address => Student) public students;
    
    /**
     * Este evento é invocado quando um aluno atinge 60 créditos
     */
    event AcquiredDegree(address studentAddress);
    
    /**
     * O construtor vai:
     * Criar uma lista de alunos
     * Criar uma lista de cursos cujos creditos só poderão ser 3 ou 6
     */
    constructor(address[] studentAddresses, uint8[] courseCredits) public {
        school = msg.sender;
        start = now;
        studentsSetup(studentAddresses);
        coursesSetup(courseCredits);
    }
    

    /**
     * As escolas chama esta função para adicionar novos alunos.
     * Esta inserção sõ é possível dentro das primeiras 4 semanas
     * da criação do contrato.
     */
    function addStudent(address studentAddress) public {
        if(isContactValid() && isStudentSubmissionAvailable()) {
            if(msg.sender == school && students[studentAddress].student == 0) {
                students[studentAddress] = Student(studentAddress, 0, 0);
            }
        }
    }
    
    /**
     * As escolas podem assignar professores aos cursos dentro
     * da primeira semana após a criação do contrato.
     */
    function assignProfessor(uint courseId, address professor) public {
        if(isContactValid() && isProfessorAssignationAvailable()) {
            if(msg.sender == school && courseId < courses.length) {
                    courses[courseId].professor = professor;
            }
        }
    }
    
    /**
     * Esta função regista o aluno que a invocou no curso em parâmetro.
     */ 
    function registerOnCourse(uint courseId) public payable {
        if(isContactValid()) {
            if(students[msg.sender].student != 0 && courseId < courses.length) {
                courses[courseId].grades[msg.sender] = -1;
                uint64 cost = computeCost(courseId);
                registerStudent(cost, courseId);
            }
        }
    }
    
    /**
     * Esta função é invocada pelos alunos de forma a se submeterem a uma reavaliação.
     * O custo por esta operação é de 0.1 ether.
     */
    function askReavaluation(uint courseId) public payable {
        if(isContactValid()) {
            if(students[msg.sender].student != 0) {
                int grade = courses[courseId].grades[msg.sender];
                if(!isPassed(grade) && grade >= 0) {
                    courses[courseId].grades[msg.sender] = -2;
                    performPayment(msg.sender, school, 0.1 ether);
                }
            }
        }
    }

    /**
     * Esta função atribui notas a um determinado conjunto de alunos.
     * As notas dos alunos estão correlacionadas relativamente ao seu índice.
     */
    function assignGrade(uint courseId, address[] evaluatedStudents, int[] grades) public payable {
        if(isContactValid()) {
            if(courses[courseId].professor == msg.sender && evaluatedStudents.length == grades.length) {
                for(uint i = 0; i < evaluatedStudents.length; i++) {
                    if(isReavaluation(courses[courseId].grades[evaluatedStudents[i]])) {
                        reavaluateStudent(courseId, grades[i]);
                    }
                    assignGrade(courseId, evaluatedStudents[i], grades[i]);
                }
            }
        }
    }

    /**
     * Vai criar os cursos que têm 3 ou 6 créditos
     */
    function coursesSetup(uint8[] courseCredits) private {
        for(uint i = 0; i < courseCredits.length; i++) {
            if(courseCredits[i] == 3 || courseCredits[i] == 6) {
                courses.push(Course(courseCredits[i], 0));
            }
        }
    }
    
    /**
     * Vai criar os cursos que têm 3 ou 6 créditos
     */
    function studentsSetup(address[] studentAddresses) private {
        for(uint i = 0; i < studentAddresses.length; i++) {
            students[studentAddresses[i]] = Student(studentAddresses[i], 0, 0);
        }
    }
    
    /**
     * Calcula o custo da inscrição da discinpla.
     * Se o aluno ja se inscreveu aos 60 créditos, os restantes serão cobrados
     * a 0.1 ether por crédito. Caso contário o custo será 0.
     */ 
    function computeCost(uint courseId) private constant returns(uint64) {
        uint64 cost = 0;
        if(students[msg.sender].registeredCredits >= 60) {
            cost = courses[courseId].credits * (0.1 ether);
        }
        return cost;
    }
    
    /**
     * Esta função processa os pagamentos e devolve o excedente da operação
     */
    function performPayment(address sender, address receiver, uint ammount) private {
        if(msg.sender.balance >= ammount) {
            uint refound = msg.value - ammount;
            receiver.transfer(ammount);
            sender.transfer(refound);
        }
    }
    
    /**
     * Inscreve o aluno no curso, incrementa os créditos e finalmente faz
     * o pagamento relativament ao custo da inscrição.
     */ 
    function registerStudent(uint64 cost, uint courseId) private {
        courses[courseId].grades[msg.sender] = -1;
        students[msg.sender].registeredCredits += courses[courseId].credits;
        performPayment(msg.sender, school, cost);
    }
    
    /**
     * Esta função atribui uma determinada nota a um aluno desde que
     * esteja inscrito, ou em processo de reavaliação.
     */ 
    function assignGrade(uint courseId, address evaluatedStudent, int grade) private {
        if(courses[courseId].grades[evaluatedStudent] < 0) {
            courses[courseId].grades[evaluatedStudent] = grade;
            if(isPassed(grade)) {
                uint8 courseCredits = courses[courseId].credits;
                students[evaluatedStudent].approvedCredits += courseCredits;
                checkDegree(evaluatedStudent);
            }
        }
    }
    
    /**
     * Esta função vai verificar se o aluno que foi submetido a reavaliação
     * foi aprovado. Se sim, o professor recebe 0.05 como prémio
     */
    function reavaluateStudent(uint courseId, int grade) private {
        if(isPassed(grade)) {
            performPayment(school, courses[courseId].professor, 0.05 ether);
        }
    }
    
    /**
     * Esta função retorna true se o aluno aprovou, caso contrário
     * retornará false.
     */ 
    function isPassed(int grade) private pure returns(bool) {
        return grade >= 10;
    }
    
    /**
     * Esta função retorna true se o aluno estiver em reavaliação, caaso contrário
     * retornará false.
     */
    function isReavaluation(int grade) private pure returns(bool) {
        return grade == -2;
    }
    
    /**
    * Esta funcao retorna true se a validade ainda se verificar, caso contrário
    * retornará false.
    */ 
    function isTimeValid(uint validity) private view returns(bool) {
    now < (start + validity);
    }
    
    /**
    * Esta funcao verifica se o contrato ainda está dentro da validade,
    * 1 ano -> 52 semanas
    */
    function isContactValid() private view returns(bool) {
     return isTimeValid(52 weeks);
    }
    
    /**
    * Esta retorna true se os alunos ainda puderem ser inscritos, caso contrário
    * retornará false.
    */
    function isStudentSubmissionAvailable() private view returns(bool) {
     return isTimeValid(4 weeks);
    }
    
    /**
    * Esta funcao retorna true se a escola ainda puder atribuir um professor
    * ao curso, caso contrário retornará false
    */
    function isProfessorAssignationAvailable() private view returns(bool) {
      return isTimeValid(1 weeks);
    }
    
    /**
     * Esta função é invocada sempre que o aluno aprova a uma disciplina.
     * Assim que o aluno atinja os 60 créditos, será despoletado um evento.
     */ 
    function checkDegree(address evaluatedStudent) private {
        if(students[evaluatedStudent].approvedCredits >= 60) {
            emit AcquiredDegree(evaluatedStudent);
        }
    }
    
}
