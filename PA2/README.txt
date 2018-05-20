#################################
# Trabalho Realizado por:
# 
# Miguel Tavares - 50691
# Sara Nascimento - 45678
# Nuno Nelas - 51691
#
#################################

# O nosso trabalho tem 1 construtor para inicializar o contrato, 5 funções que são utilizadas
# para realizar as operações especificadas nos requisitos, e um evento que é lançado assim que
# um aluno atinge 60 créditos aprovados.

#### ESCOLA ####

# Esta função é responsável por inicializar o contrato.
# Como primeiro argumento o construtor recebe um array de address correspondente aos alunos.
# O segundo argumento é também um array com os créditos respeitantes a cada curso.
# O ID do curso é dado pelo seu indice no array de créditos.
--> constructor(address[] studentAddresses, uint8[] courseCredits) public 

# Esta função permite à escola adicionar novos alunos.
# Só é possível adicionar novos alunos dentro do primeiro mês de validade do contrato.
# Recebe como argumento o address correspondente ao aluno a adicionar.
--> function addStudent(address studentAddress) public

# Esta função permite à escola atribuir um professor a um determinado curso.
# Esta atribuição só é possível de ser feita dentro da primeira semana da validade do contrato.
--> function assignProfessor(uint courseId, address professor) public

# Esta função vai processar os pagamentos relativos ao trabalho extra dos
# professores. Por acada aluno que reprovado e posteriormente foi aprovado
# em reavaliação, o professor ganha 0.05 ether. É a escola que processa este pagamento.
# Como argumento esta função recebe o address do professor que vai receber o pagamento.
--> function payForApprovals(address professor) public payable

#### PROFESSOR ####

# Esta função permite ao professor lançar as notas dos alunos relativas a um determinado curso.
# O primeiro argumento da função é o ID do curso em questão.
# O segundo argumento é um array de address que referencia os alunos a serem avaliados.
# Por fim, o último argumento é um array de inteiros que diz respeito às notas dos alunos.
# Os alunos e as notas estão associadas pelo indice em cada um dos arrays.
--> function assignGrade(uint courseId, address[] evaluatedStudents, int[] grades) public payable

#### ALUNO ####

# Esta função permite ao aluno inscrever-se num determinado curso.
# O argumento passado nesta função diz respeito ao id do curso em que o aluno se quer inscrever.
--> function registerOnCourse(uint courseId) public payable

# Esta função permite aos alunos solicitarem uma reavaliação a um determinado curso.
# O argumento passado nesta função diz respeito ao id do curso em que o aluno pretende ser reavaliado.
--> function askReavaluation(uint courseId) public payable

# Este evento é lançado no momento em que um aluno atinge 60 créditos.
# O argumento existente neste evento diz respeito ao address do aluno em questão.
--> event AcquiredDegree(address studentAddress)

#### AVALIAÇÃO / INSCRIÇÃO ####

# O estado de avaliação / inscrição de um aluno reflete-se na sua nota.
# Um aluno quando não está inscrito tem como nota "0"
# Um aluno que está inscrito a um curso mas que ainda nao foi avaliado tem como nota "-1"
# E por fim, um aluno que está num processo de reavaliação terá como nota "-2"

