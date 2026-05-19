package com.ganesh.skillbridge.util;

import com.ganesh.skillbridge.entity.*;
import com.ganesh.skillbridge.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final CompanyRepository companyRepo;
    private final QuestionRepository questionRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-data:true}")
    private boolean seedData;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedData) return;
        if (companyRepo.count() == 0) {
            log.info("🌱 Seeding companies and questions...");
            seedCompaniesAndQuestions();
        } else {
            log.info("✅ Companies already seeded.");
        }
        seedAdminUser();
        log.info("✅ Seeding complete.");
    }

    private void seedAdminUser() {
        final String adminEmail = "admin@skillbridge.dev";
        userRepo.findByEmailIgnoreCase(adminEmail).ifPresentOrElse(admin -> {
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepo.save(admin);
                log.info("👤 Admin password reset: {} / admin123", adminEmail);
            } else {
                log.info("👤 Admin user ready: {} / admin123", adminEmail);
            }
        }, () -> {
            userRepo.save(User.builder()
                .name("Admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("admin123"))
                .role("ADMIN")
                .build());
            log.info("👤 Admin user created: {} / admin123", adminEmail);
        });
    }

    private void seedCompaniesAndQuestions() {

        // ── TCS ───────────────────────────────────────────────────────────────
        Company tcs = companyRepo.save(Company.builder()
            .name("TCS")
            .description("Tata Consultancy Services — India's largest IT company")
            .difficultyLevel("Medium")
            .avgPackage("3.5 - 7 LPA")
            .testPattern("NQT: Verbal, Aptitude, Reasoning, Coding (2 problems)")
            .questionCount(0)
            .build());

        List<Question> tcsQ = List.of(
            q(tcs,"If a train travels 360 km at 90 km/h, how long does it take?",
              "Calculate time = distance / speed","Aptitude","Time & Work","Easy",
              "4 hours","360/90 = 4 hours","2 hours","4 hours","6 hours","8 hours","MCQ"),

            q(tcs,"What is the output of: int x=5; System.out.println(x++ + ++x);",
              "Pre and post increment in Java","Java","Core Java","Medium",
              "12","x++ gives 5 (x becomes 6), ++x gives 7 → 5+7=12","10","11","12","13","MCQ"),

            q(tcs,"Write a SQL query to find the second highest salary from Employee table.",
              "Use subquery or LIMIT","SQL","Queries","Medium",
              "SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee)",
              "Use nested MAX or DENSE_RANK()","","","","","SQL"),

            q(tcs,"In a bag there are 5 red, 3 blue and 2 green balls. What is the probability of picking a red ball?",
              "P = favorable/total","Aptitude","Probability","Easy",
              "1/2","5/10 = 1/2","1/3","1/2","2/5","3/10","MCQ"),

            q(tcs,"Tell me about yourself.",
              "Classic HR opener — structure: Present, Past, Future","HR","Introduction","Easy",
              "A well-structured 90-second answer covering education, skills, projects, and career goal.",
              "Start with your name, education, projects, and why you're interested in TCS.",
              "","","","","TEXT"),

            q(tcs,"Reverse a string without using built-in reverse().",
              "Implement iterative or recursive reversal","DSA","Strings","Easy",
              "Use a loop from end to start or two-pointer swap",
              "Two-pointer: swap chars at i and n-1-i until i < n/2","","","","","CODE"),

            q(tcs,"What is polymorphism in Java?",
              "OOP concept","Java","OOP","Easy",
              "Polymorphism allows one interface to be used for different data types — compile-time (overloading) and runtime (overriding).",
              "Give example of method overloading and overriding","","","","","TEXT"),

            q(tcs,"A sum of Rs.12,000 amounts to Rs.15,000 in 4 years at SI. Find the rate%.",
              "SI = PRT/100","Aptitude","Simple Interest","Easy",
              "6.25%","SI=3000, R=3000*100/(12000*4)=6.25%","5%","6%","6.25%","7%","MCQ")
        );
        questionRepo.saveAll(tcsQ);
        tcs.setQuestionCount(tcsQ.size());
        companyRepo.save(tcs);

        // ── Infosys ───────────────────────────────────────────────────────────
        Company infosys = companyRepo.save(Company.builder()
            .name("Infosys")
            .description("Infosys — Global IT services leader, known for reasoning-heavy tests")
            .difficultyLevel("Medium")
            .avgPackage("3.6 - 8 LPA")
            .testPattern("Aptitude, Reasoning, Verbal, Pseudo Code, Coding")
            .questionCount(0)
            .build());

        List<Question> infosysQ = List.of(
            q(infosys,"Find the missing number in: 2, 6, 12, 20, 30, ?",
              "Pattern: n*(n+1)","Aptitude","Number Series","Easy",
              "42","Pattern: 1×2, 2×3, 3×4, 4×5, 5×6, 6×7=42","36","40","42","44","MCQ"),

            q(infosys,"What does ACID stand for in databases?",
              "DB transactions","SQL","Transactions","Medium",
              "Atomicity, Consistency, Isolation, Durability",
              "Each property ensures reliable transaction processing","","","","","TEXT"),

            q(infosys,"Write SQL to find all employees who earn more than their manager.",
              "Self join on Employee table","SQL","Joins","Hard",
              "SELECT e.name FROM Employee e JOIN Employee m ON e.manager_id=m.id WHERE e.salary > m.salary",
              "Self-join where employee salary > manager salary","","","","","SQL"),

            q(infosys,"What is the difference between == and .equals() in Java?",
              "Reference vs value comparison","Java","Core Java","Easy",
              "== compares references (memory addresses); .equals() compares actual content/values.",
              "Especially important for String comparison","","","","","TEXT"),

            q(infosys,"Why do you want to join Infosys?",
              "Research the company before answering","HR","Motivation","Easy",
              "Focus on Infosys's global presence, training programs, learning culture, and tech vision.",
              "Mention InfyTQ, Lex platform, global clients","","","","","TEXT"),

            q(infosys,"Two pipes A and B can fill a tank in 20 and 30 minutes. Both opened together — time to fill?",
              "Combined rate = 1/20 + 1/30","Aptitude","Pipes & Cisterns","Medium",
              "12 minutes","1/20+1/30=5/60=1/12, so 12 minutes","10","12","15","18","MCQ"),

            q(infosys,"Implement binary search on a sorted array.",
              "O(log n) search algorithm","DSA","Searching","Medium",
              "Compare mid element; go left if target < mid, right if target > mid",
              "Classic divide-and-conquer approach","","","","","CODE"),

            q(infosys,"What is the difference between ArrayList and LinkedList?",
              "Java Collections","Java","Collections","Medium",
              "ArrayList uses dynamic array (O(1) access, O(n) insert); LinkedList uses doubly linked nodes (O(n) access, O(1) insert at ends).",
              "Also mention memory overhead of LinkedList","","","","","TEXT")
        );
        questionRepo.saveAll(infosysQ);
        infosys.setQuestionCount(infosysQ.size());
        companyRepo.save(infosys);

        // ── Wipro ──────────────────────────────────────────────────────────────
        Company wipro = companyRepo.save(Company.builder()
            .name("Wipro")
            .description("Wipro — IT services with NLTH hiring for freshers")
            .difficultyLevel("Easy")
            .avgPackage("3.5 - 6.5 LPA")
            .testPattern("Aptitude, Reasoning, Verbal, Essay, Coding")
            .questionCount(0)
            .build());

        List<Question> wiproQ = List.of(
            q(wipro,"A can do a work in 15 days and B in 20 days. Together they finish in?",
              "Combined rate work problem","Aptitude","Time & Work","Easy",
              "60/7 ≈ 8.57 days","1/15+1/20=7/60 so 60/7 days","8","60/7","10","12","MCQ"),

            q(wipro,"What is method overriding in Java?",
              "Runtime polymorphism","Java","OOP","Easy",
              "When a subclass provides a specific implementation of a method declared in its parent class with the same signature.",
              "Use @Override annotation for safety","","","","","TEXT"),

            q(wipro,"Select department names that have more than 5 employees.",
              "GROUP BY + HAVING","SQL","Aggregation","Medium",
              "SELECT dept_name FROM employees GROUP BY dept_name HAVING COUNT(*) > 5",
              "HAVING filters after GROUP BY; WHERE filters before","","","","","SQL"),

            q(wipro,"Find the largest element in an array without using sort.",
              "Linear scan O(n)","DSA","Arrays","Easy",
              "Traverse and maintain a max variable; update if current > max",
              "Single pass — O(n) time, O(1) space","","","","","CODE"),

            q(wipro,"What are your strengths and weaknesses?",
              "Self-awareness HR question","HR","Behavioral","Easy",
              "Pick a genuine strength relevant to tech role. Mention a weakness you're actively improving.",
              "Don't say 'I'm a perfectionist' — it's clichéd","","","","","TEXT")
        );
        questionRepo.saveAll(wiproQ);
        wipro.setQuestionCount(wiproQ.size());
        companyRepo.save(wipro);

        // ── Zoho ──────────────────────────────────────────────────────────────
        Company zoho = companyRepo.save(Company.builder()
            .name("Zoho")
            .description("Zoho — Product-based company with intense coding rounds")
            .difficultyLevel("Hard")
            .avgPackage("5 - 12 LPA")
            .testPattern("3-5 rounds: Written → Technical 1 → Technical 2 → HR")
            .questionCount(0)
            .build());

        List<Question> zohoQ = List.of(
            q(zoho,"Find all pairs in an array that sum to a given target.",
              "Two-pointer or HashMap approach","DSA","Arrays","Medium",
              "Use HashMap: for each element, check if (target-element) exists in map. O(n) time.",
              "Two-pointer works on sorted arrays","","","","","CODE"),

            q(zoho,"Explain the concept of HashMap internal working in Java.",
              "Java internals","Java","Collections","Hard",
              "HashMap uses array of buckets. Key.hashCode() determines bucket index. Collision handled by LinkedList (Java 7) or Red-Black Tree (Java 8+, when bucket size > 8).",
              "Mention load factor (0.75) and rehashing","","","","","TEXT"),

            q(zoho,"Write SQL to find customers who placed orders in every month of 2024.",
              "Complex aggregation","SQL","Advanced Queries","Hard",
              "SELECT customer_id FROM orders WHERE YEAR(order_date)=2024 GROUP BY customer_id HAVING COUNT(DISTINCT MONTH(order_date))=12",
              "COUNT DISTINCT MONTH trick","","","","","SQL"),

            q(zoho,"What motivates you to join a product company like Zoho?",
              "Product vs service mindset","HR","Motivation","Medium",
              "Talk about wanting to build products used by millions, ownership of features, long-term impact, and Zoho's culture of bootstrapping and innovation.",
              "Mention specific Zoho products you use","","","","","TEXT"),

            q(zoho,"Implement a stack using two queues.",
              "Classic data structure problem","DSA","Stack & Queue","Hard",
              "Push: enqueue to q1. Pop: move all except last from q1 to q2, dequeue last from q1, swap q1 and q2.",
              "O(n) push or O(n) pop depending on approach","","","","","CODE")
        );
        questionRepo.saveAll(zohoQ);
        zoho.setQuestionCount(zohoQ.size());
        companyRepo.save(zoho);

        // ── Amazon ─────────────────────────────────────────────────────────────
        Company amazon = companyRepo.save(Company.builder()
            .name("Amazon")
            .description("Amazon — FAANG; Leadership Principles + DSA heavy")
            .difficultyLevel("Hard")
            .avgPackage("15 - 35 LPA")
            .testPattern("OA: DSA × 2 → Technical × 2 → Bar Raiser → HR")
            .questionCount(0)
            .build());

        List<Question> amazonQ = List.of(
            q(amazon,"Find the longest substring without repeating characters.",
              "Sliding window technique","DSA","Strings","Hard",
              "Use sliding window with a HashSet. Expand right pointer, shrink left when duplicate found. O(n).",
              "Classic sliding window — must know for FAANG","","","","","CODE"),

            q(amazon,"Tell me about a time you showed ownership at work/college.",
              "Amazon Leadership Principle: Ownership","HR","Leadership Principles","Medium",
              "Use STAR method: Situation, Task, Action, Result. Show you went beyond your role.",
              "Amazon values Ownership above all — give a real example","","","","","TEXT"),

            q(amazon,"What is the difference between SQL JOIN types?",
              "INNER, LEFT, RIGHT, FULL","SQL","Joins","Medium",
              "INNER: matching rows only. LEFT: all left + matching right. RIGHT: all right + matching left. FULL: all rows from both with NULLs where no match.",
              "Draw a Venn diagram mentally","","","","","TEXT"),

            q(amazon,"What is the time complexity of quicksort in worst case?",
              "Algorithm analysis","DSA","Sorting","Medium",
              "O(n²) — occurs when pivot is always the smallest or largest element (sorted/reverse-sorted input). Average: O(n log n).",
              "Mitigate with random pivot selection","O(n)","O(n log n)","O(n²)","O(n³)","MCQ")
        );
        questionRepo.saveAll(amazonQ);
        amazon.setQuestionCount(amazonQ.size());
        companyRepo.save(amazon);

        // ── Accenture ─────────────────────────────────────────────────────────
        Company accenture = companyRepo.save(Company.builder()
            .name("Accenture")
            .description("Accenture — Global consulting & IT; moderate difficulty for freshers")
            .difficultyLevel("Easy")
            .avgPackage("4 - 8 LPA")
            .testPattern("Cognitive + Technical + Coding + Communication Assessment")
            .questionCount(0)
            .build());

        List<Question> accentureQ = List.of(
            q(accenture,"What is the difference between abstract class and interface in Java?",
              "Core OOP concepts","Java","OOP","Medium",
              "Abstract class can have concrete methods, constructors, state. Interface (Java 8+) can have default/static methods, no state. A class can implement multiple interfaces but extend only one class.",
              "Java 8 added default methods to interfaces","","","","","TEXT"),

            q(accenture,"A train 150m long passes a pole in 15s. Find speed in km/h.",
              "Speed = distance/time, convert to km/h","Aptitude","Speed & Distance","Easy",
              "36 km/h","150/15=10 m/s × 18/5 = 36 km/h","30","36","40","45","MCQ"),

            q(accenture,"Write SQL to delete duplicate rows keeping only one.",
              "Delete with ROW_NUMBER or MIN(id)","SQL","Data Manipulation","Hard",
              "DELETE FROM table WHERE id NOT IN (SELECT MIN(id) FROM table GROUP BY duplicate_column)",
              "Use CTE with ROW_NUMBER for cleaner approach","","","","","SQL"),

            q(accenture,"Where do you see yourself in 5 years?",
              "Career growth HR question","HR","Career Goals","Easy",
              "Focus on skill development, growing into a tech lead or solution architect, and contributing to impactful projects. Align with company's growth path.",
              "Show ambition but also loyalty to the company","","","","","TEXT")
        );
        questionRepo.saveAll(accentureQ);
        accenture.setQuestionCount(accentureQ.size());
        companyRepo.save(accenture);

        // ── Cognizant ─────────────────────────────────────────────────────────
        Company cognizant = companyRepo.save(Company.builder()
            .name("Cognizant")
            .description("Cognizant — IT services; GenC and GenC Pro tracks")
            .difficultyLevel("Easy")
            .avgPackage("3.5 - 6 LPA")
            .testPattern("Aptitude + Verbal + Logical + Coding")
            .questionCount(0)
            .build());

        List<Question> cognizantQ = List.of(
            q(cognizant,"What is the output: String s1='hello'; String s2='hello'; System.out.println(s1==s2);",
              "String pool in Java","Java","Core Java","Medium",
              "true","String literals go to string pool; same literal refers to same object",
              "false","true","Compilation error","Runtime error","MCQ"),

            q(cognizant,"If 20 men can build a wall in 30 days, how long will 15 men take?",
              "Inverse proportion: men × days = constant","Aptitude","Time & Work","Easy",
              "40 days","20×30=15×d → d=40","30","35","40","45","MCQ"),

            q(cognizant,"What is normalization in databases?",
              "DB design concept","SQL","Database Design","Medium",
              "Normalization organizes a database to reduce redundancy and improve integrity. Normal forms: 1NF (atomic values), 2NF (no partial dependency), 3NF (no transitive dependency).",
              "Know 1NF, 2NF, 3NF at minimum","","","","","TEXT"),

            q(cognizant,"Check if a number is a palindrome without converting to string.",
              "Math-based palindrome check","DSA","Numbers","Easy",
              "Reverse the number mathematically using modulo and compare with original.",
              "Handle negative numbers — they can't be palindromes","","","","","CODE")
        );
        questionRepo.saveAll(cognizantQ);
        cognizant.setQuestionCount(cognizantQ.size());
        companyRepo.save(cognizant);

        // ── Capgemini ─────────────────────────────────────────────────────────
        Company capgemini = companyRepo.save(Company.builder()
            .name("Capgemini")
            .description("Capgemini — IT + consulting; GAME test for freshers")
            .difficultyLevel("Medium")
            .avgPackage("4 - 7 LPA")
            .testPattern("GAME: Psychometric + Technical + Coding + Essay")
            .questionCount(0)
            .build());

        List<Question> capgeminiQ = List.of(
            q(capgemini,"What is a deadlock in OS and how can it be prevented?",
              "OS concept","Java","Multithreading","Hard",
              "Deadlock: two threads wait for each other's lock indefinitely. Prevent using: lock ordering, timeout, deadlock detection algorithms.",
              "Coffman conditions: Mutual exclusion, Hold & Wait, No preemption, Circular wait","","","","","TEXT"),

            q(capgemini,"Find the LCM of 12 and 18.",
              "LCM using prime factorization or GCD","Aptitude","Number System","Easy",
              "36","LCM=12×18/GCD(12,18)=216/6=36","24","36","48","72","MCQ"),

            q(capgemini,"Write SQL to get the count of employees per department, sorted by count descending.",
              "GROUP BY with ORDER BY","SQL","Aggregation","Easy",
              "SELECT department, COUNT(*) as emp_count FROM employees GROUP BY department ORDER BY emp_count DESC",
              "Classic GROUP BY problem","","","","","SQL"),

            q(capgemini,"Are you comfortable relocating?",
              "Flexibility HR question","HR","Personal","Easy",
              "Be honest but flexible. If yes, say you're open to any location for the right opportunity. Mention any specific constraint only if critical.",
              "Never say a flat no in the first round","","","","","TEXT")
        );
        questionRepo.saveAll(capgeminiQ);
        capgemini.setQuestionCount(capgeminiQ.size());
        companyRepo.save(capgemini);

        log.info("✅ Seeded 7 companies with {} total questions",
            tcsQ.size() + infosysQ.size() + wiproQ.size() + zohoQ.size() +
            amazonQ.size() + accentureQ.size() + capgeminiQ.size());
    }

    private Question q(Company company, String title, String desc, String category,
                        String topic, String difficulty, String correctAnswer,
                        String explanation, String a, String b, String c, String d, String type) {
        return Question.builder()
            .company(company)
            .title(title)
            .description(desc)
            .category(category)
            .topic(topic)
            .difficulty(difficulty)
            .correctAnswer(correctAnswer)
            .explanation(explanation)
            .optionA(a).optionB(b).optionC(c).optionD(d)
            .questionType(type)
            .points(difficulty.equals("Hard") ? 20 : difficulty.equals("Medium") ? 15 : 10)
            .build();
    }
}
