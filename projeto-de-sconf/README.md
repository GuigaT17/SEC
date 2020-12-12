# Projeto de SConf

Projeto de SConf

**INSTRUÇÕES (leiam, senão isto vai dar merda)**

Para começar recomendo gastarem 10 minutos para fazerem alguns tutoriais do learngitbranching.js.org que tambem tem um bom modo sandbox para perceber isto.
Recomendo pelo menos primeiros 3 de Main e os primeiros 6 de Remote, no minimo. É seca, mas explica bem.

Para gerirmos isto de uma forma meio decente, vamos ter que usar branches. Ou seja cada um de nós vai ter um branch chamado branchXXXXX, substituindo o nosso nome nos XXXXX. 
Isto é para garantir que mesmo que nós trabalhemos nos mesmos ficheiros, podemos continuar a trabalhar independentemente, e depois podemos dar merge nos branches e combinar o codigo de duas versões do ficheiro numa só (o git tem um mecanismo para tratar de conflictos, em que há codigos diferentes nas mesmas partes/linhas do ficheiro, depois elaboro).

Então heis as instruções para trabalhar nisto (notem, se o professor der ficheiros, metam no master antes de começarem a fazer branches, fica mais simples)

1. Ligar à VPN (obviamente) 
2. git clone https://git.alunos.di.fc.ul.pt/fc51964/projeto-de-psi (no Windows 10 deve-vos aparecer uma janela para login, no Linux acho que pede na shell)

Em ramos há duas operações: branch e checkout

git branch <nome> cria um branch no nó/commit currente

git checkout <nome> torna o nó/commit de nome o currente

Alternativamente podem combinar estes dois comandos com:

git checkout -b <nome>

Todos os projetos começam com um só branch, o master.

No inicio se já nao tiverem um branch, fazem checkout do master, e criam o vosso.

Para quando forem trabalhar
4. git checkout branchXXXXX , porque cada nó da arvóre/branch representa um commit ou seja um estado do projeto, logo só podem sacar um nó de cada vez, não é como se aparecesse em pastas cada branch ou assim
3. git pull (para terem a versão mais recente dos ficheiros) 

**NOTA: FAZER ISTO CADA VEZ QUE COMEÇAREM A MODIFICAR FICHEIROS NO GIT, SENÃO NÃO VÃO CONSEGUIR FAZER COMMIT E PUSH PORQUE NÃO TÊM A VERSÃO MAIS RECENTE E ISTO ESTÁ EM CAPS PORQUE JÁ DISSE A CERTAS PESSOAS DEMASIADAS VEZES)****

**!!!!!!!!!!!!!!!!!!!IMPORTANTE!!!!!!!!!!!!!!!!!**

Se quiserem continuar com a versão mais recente do projeto (o master, não o vosso ultimo progresso) fazem:
5. git merge master

Nota: têm de estar no vosso branch

Isto merge a versão mais recente com o branch onde vão trabalhar. Não façam o oposto de estar no master e mergir com o vosso, porque não dá update ao vosso , só ao master, e depois pode gerar problemas. 
Escolhem um nó, fazem checkout e o merge vai puxar de um nó para o que está no checkout, criando um novo nó/commit para que o que estava previamente no checkout aponta.

Se quiserem só continuar no vosso próprio ramo, fazem checkout do vosso ramo, só para garantir que lá estão.

Para meterem código vosso no master, ou seja, adicionar uma feature ou assim que já sabem que está certa/mais ninguém está a implementar:

7. git checkout master
8. git merge branchXXXXX com o vosso branch

E assim copiaram os vosso código para o master. NOTA IMPORTANTE: O merge substitui todas as alterações feitas entre os commits. Ou seja faz de conta que o master e o branchCarlos têm dois ficheiros 1 e 2. 
Eu faço muito progresso no ficheiro 1 e quero meter no master. Mas também cortei/comentei/modifiquei código do 2, que nem era meu, não era para fazer isso, só estava a testar ou assim. 
Nem estou a trabalhar no ficheiro 2, foi outra pessoa que o fez e está encarregada. Logo tenho de ter cuidado para não fazer o merge imediatamente e ir obter a versão mais recente do ficheiro 2 antes de fazer merge dos dois ficheiros.

**!!!!!!!!!!!!CONFLITOS NO GIT!!!!!!!!!!!!!!!**

Isto toca no que foi mencionado em cima. Vou só copiar de https://www.atlassian.com/git/tutorials/using-branches/git-merge porque explica bem, vocês sabem inglês. O que está com % é o que aparece na linha de comandos, menos o % obviamente.

**~~~~~~~~~~~~Resolving conflict~~~~~~~~~~~~~~**

If the two branches you're trying to merge both changed the same part of the same file, Git won't be able to figure out which version to use. When such a situation occurs, it stops right before the merge commit so that you can resolve the conflicts manually.

The great part of Git's merging process is that it uses the familiar edit/stage/commit workflow to resolve merge conflicts. When you encounter a merge conflict, running the git status command shows you which files need to be resolved. For example, if both branches modified the same section of hello.py, you would see something like the following:

% On branch master

% Unmerged paths:

% (use "git add/rm ..." as appropriate to mark resolution)

% both modified: hello.py

**~~~~~~~~~~How conflicts are presented~~~~~~~~~~~~~~**

When Git encounters a conflict during a merge, It will edit the content of the affected files with visual indicators that mark both sides of the conflicted content. These visual markers are: <<<<<<<, =======, and >>>>>>>. Its helpful to search a project for these indicators during a merge to find where conflicts need to be resolved.

% here is some content not affected by the conflict

% <<<<<<< master

% this is conflicted text from master

% =======

% this is conflicted text from feature branch

% >>>>>>> feature branch

Generally the content before the ======= marker is the receiving branch and the part after is the merging branch.

Once you've identified conflicting sections, you can go in and fix up the merge to your liking. When you're ready to finish the merge, all you have to do is run git add on the conflicted file(s) to tell Git they're resolved. Then, you run a normal git commit to generate the merge commit. It’s the exact same process as committing an ordinary snapshot, which means it’s easy for normal developers to manage their own merges.


**!!!!!!!!!!!!METER MUDANÇAS NO GIT!!!!!!!!!!!!**

9. git checkout branchXXXXX (em principio só vão fazer commits no vosso branch, e o master só recebe updates por merge)
10. git add <ficheiro> para cada ficheiro que querem guardar as alterações. Podem usar git status para ver os ficheiros alterados e a flag -A (maisucula mesmo) para meterem todos os ficheiros alterados. Podem ver https://git-scm.com/docs/git-add para ajudar
11. git commit -M "mensagem que explica" (e sejam mesmo programadores e expliquem para não termos de andar sempre no whatsapp)
12. git push para colocar no GitLab

**NOTAS FINAIS:** Se não souberem o que estão a fazer, mandem mensagem em vez de estragar algo que não deviam, não sou pro nisto, mas posso ir ver. E se algo não estiver a dar como estavam à espera avisem, para poder explicar, ou para corrigir algo, se ficou mal configurado ou se percebi mal. 

