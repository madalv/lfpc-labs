package labs.lab3;

public class Lab3 {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        String code = """
               fun addValues(array<int> a) ret int {
               int sum = 0;
               for (int i = 0 to length(a); i++) {
                    sum += a[i];
                    }
               }
                                
               fun main() ret void {
                    array<int> a = {1, 2, 3, 5};      
                    int sum = addValues(a);
               }""";

        String code2 = """
                // recursive function
                                
                fun fib(int n) ret int {
                	if (n == 0) return 0;
                	if (n == 1) return 1;
                	else fib(n - 1) + fib(n - 2);
                }
                
                string s = "aaa";
                """;

        lexer.tokenize(code2).forEach(a -> System.out.println(a.type + " " + a.literal));
    }
}
