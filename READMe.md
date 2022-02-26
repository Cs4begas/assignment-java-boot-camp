# Assignments for Java Boot Camp
* [Week 1 :: Design and Develop RESTful API with Spring Boot](https://github.com/up1/assignment-java-boot-camp/wiki/Week-01)

## Shopping-flow
###Requirement 
   1. สามารถ Search product ด้วยชื่อ Product ได้
   2. การสร้าง Basket (ตะกร้าสินค้า) สามารถใส่สินค้าได้เพียงอย่างเดียว
   3. จะ Checkout Basket ได้ต้องผ่านการสร้าง Basket มาก่อน
   4. จะ Confirm-shipping ต้องผ่านการสร้าง Checkout และ Status ของ Basket เป็น Checkout
   5. จะ Confirm-Order ต้องผ่านการ Confirm-shipping มาก่อน และ Status ของ Basket เป็น Confirm-Shipping
   6. จะดู Summary ต้องผ่านการ Confirm-Order และ Status ของ Basket เป็น Confirm-Order มาก่อน
   7. การจ่ายเงินมี 3 แบบคือ 1. บัตรเครดิต 2. เก็บเงินปลายทาง 3. บัตรเดบิต (Happy flow ใช้ บัตรเครดิต)
###Flow and Requirement Design
   * [FlowAndRequirementDesign](https://miro.com/app/board/uXjVOMArmLs=/)
###Database design
   
###Installation


## Resources
* [Spring Boot Reference](https://spring.io/projects/spring-boot)
* https://www.baeldung.com/ 
* https://start.spring.io/
* [Git commit message](https://www.conventionalcommits.org/en/v1.0.0/)






```mermaid
sequenceDiagram
    actor Alice
    actor Bob
    Alice->>Bob: Hi Bob
    Bob->>Alice: Hi Alice
```
