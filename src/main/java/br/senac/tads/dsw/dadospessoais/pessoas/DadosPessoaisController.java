package br.senac.tads.dsw.dadospessoais.pessoas;

import br.senac.tads.dsw.dadospessoais.comentario.Comentario;
import br.senac.tads.dsw.dadospessoais.comentario.ComentarioDto;
import br.senac.tads.dsw.dadospessoais.comentario.ComentarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pessoas")
public class DadosPessoaisController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @GetMapping
    public ModelAndView listar() {
        List<DadosPessoais> resultados = dadosPessoaisRepository.findAll();
        ModelAndView mv = new ModelAndView("lista");
        mv.addObject("dadosLista", resultados);
        return mv;
    }

    @GetMapping("/{id}/visualizar")
    public ModelAndView visualizar(
            @PathVariable("id") Integer id,
            RedirectAttributes redirAttr) {
        Optional<DadosPessoais> optPessoa = dadosPessoaisRepository.findById(id);
        if (!optPessoa.isPresent()) {
            redirAttr.addFlashAttribute("msgErro", "Pessoa com ID " + id + " não encontrada");
            return new ModelAndView("redirect:/pessoas");
        }
        List<Comentario> comentarioLista = comentarioRepository.findByPessoa(optPessoa.get());
        ModelAndView mv = new ModelAndView("visualizacao");
        mv.addObject("dados", optPessoa.get());
        mv.addObject("lista", comentarioLista);
        mv.addObject("comentario", new Comentario());
        return mv;
    }

    @PostMapping("/salvar/{id}") //salvar no BD
    public String salvar(@ModelAttribute("comentario") ComentarioDto comentario, @PathVariable(value = "id") Integer id) {
        Comentario c = new Comentario();      
        c.setNome(comentario.getNome());
        c.setEmail(comentario.getEmail());
        c.setComentario(comentario.getComentario());
        Optional<DadosPessoais> op = dadosPessoaisRepository.findById(id);
        c.setPessoa(op.get());
        comentarioRepository.save(c);
        return "redirect:/pessoas/" + id + "/visualizar";
    }
}
