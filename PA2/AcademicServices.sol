pragma solidity ^0.4.21;

// grades  0 nao inscrito
// grades -1 nao avaliado
// grades -2 reavaliação

contract AcademicService {
    
    struct Course {
        uint8 credits;
        address professor;
        mapping(address => int) grades;
    }
    
    struct Student {
        address student;
        uint8 registeredCredits;
        uint8 approvedCredits;
    }
    
    address public school;
    uint256 public start; 
    Course[]public courses;
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
     * Vai criar os cursos que têm 3 ou 6 créditos
     */
    function studentsSetup(address[] studentAddresses) private {
        for(uint i = 0; i < studentAddresses.length; i++) {
            students[studentAddresses[i]] = Student(studentAddresses[i], 0, 0);
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
     * As escolas chama esta função para adicionar novos alunos.
     * Esta inserção sõ é possível dentro das primeiras 4 semanas
     * da criação do contrato.
     */
    function addStudent(address studentAddress) public {
        if(msg.sender == school && now < (start + 4 weeks) && students[studentAddress].student == 0) {
            students[studentAddress] = Student(studentAddress, 0, 0);
        }
    }
    
    /**
     * As escolas podem assignar professores aos cursos dentro
     * da primeira semana após a criação do contrato.
     */
    function assignProfessor(uint courseId, address professor) public {
        if(msg.sender == school && courseId < courses.length &&
            courses[courseId].professor != 0 && now < (start + 1 weeks)) {
                courses[courseId].professor = professor;
        }
    }
    
    /**
     * Esta função regista o aluno que a invocou no curso em parâmetro.
     */ 
    function registerOnCourse(uint courseId) public payable {
        if(students[msg.sender].student != 0 && courseId < courses.length) {
            courses[courseId].grades[msg.sender] = -1;
            uint64 cost = computeCost(courseId);
            registerStudent(cost, courseId);
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
     * Inscreve o aluno no curso, incrementa os créditos e finalmente faz
     * o pagamento relativament ao custo da inscrição.
     */ 
    function registerStudent(uint64 cost, uint courseId) private {
        courses[courseId].grades[msg.sender] = -1;
        students[msg.sender].registeredCredits += courses[courseId].credits;
        school.transfer(cost);
    }
    
    /**
     * Esta função atribui notas a um determinado conjunto de alunos.
     * As notas dos alunos estão correlacionadas relativamente ao seu índice.
     */
    function assignGrade(uint courseId, address[] evaluatedStudents, int[] grades) public {
        if(courses[courseId].professor == msg.sender && evaluatedStudents.length == grades.length) {
            for(uint i = 0; i < evaluatedStudents.length; i++) {
                assignGrade(courseId, evaluatedStudents[i], grades[i]);
            }
        }
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
     * Esta função retorna true se o aluno aprovou, caso contrário
     * retornará false.
     */ 
    function isPassed(int grade) private pure returns(bool) {
        return grade >= 10;
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
    
    // quem chama -> aluno
    // paga 0.1 Eth
    function askReavaluation(uint courseId) public payable {
        if(courses[courseId].grades[msg.sender] > 0) {
            
        }
    }
}
